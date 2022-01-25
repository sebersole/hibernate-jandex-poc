package org.hibernate.orm.boot.jandex.internal;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Collection;
import java.util.Comparator;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.orm.boot.jandex.spi.JandexIndexAccess;
import org.hibernate.orm.boot.spi.ResourceLocator;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.Type;

/**
 * @author Steve Ebersole
 */
public class BuildingJandexIndexAccess implements JandexIndexAccess {
	private final Indexer indexer;
	private final ResourceLocator resourceLocator;

	private final Map<String,ClassInfo> cachedInfoMap = new HashMap<>();

	public BuildingJandexIndexAccess(Indexer indexer, ResourceLocator resourceLocator) {
		this.indexer = indexer;
		this.resourceLocator = resourceLocator;

		primeIndexer();
	}

	private void primeIndexer() {
		indexClass( Boolean.class );
		indexClass( Byte.class );
		indexClass( Short.class );
		indexClass( Integer.class );
		indexClass( Long.class );
		indexClass( Float.class );
		indexClass( Double.class );
		indexClass( BigInteger.class );
		indexClass( BigDecimal.class );
		indexClass( Class.class );
		indexClass( Locale.class );
		indexClass( Currency.class );
		indexClass( URL.class );
		indexClass( UUID.class );
		indexClass( Collection.class );
		indexClass( List.class );
		indexClass( Set.class );
		indexClass( SortedSet.class );
		indexClass( Map.class );
		indexClass( SortedMap.class );
		indexClass( Comparable.class );
		indexClass( Comparator.class );
	}

	private void indexClass(Class<?> clazz) {
		indexClass( clazz.getName() );
	}

	@Override
	public ClassInfo packageInfo(String packageName) {
		final ClassInfo cached = cachedInfoMap.get( packageName );
		if ( cached != null ) {
			return cached;
		}

		final ClassInfo classInfo = indexClass( packageName + ".package-info" );
		cachedInfoMap.put( packageName, classInfo );

		return classInfo;
	}

	@Override
	public ClassInfo classInfo(String className) {
		final ClassInfo cached = cachedInfoMap.get( className );
		if ( cached != null ) {
			return cached;
		}

		final ClassInfo classInfo = indexClass( className );
		cachedInfoMap.put( className, classInfo );
		furtherProcess( classInfo );

		return classInfo;
	}

	@Override
	public ClassInfo classInfo(Class<?> classReference) {
		final String className = classReference.getName();

		final ClassInfo cached = cachedInfoMap.get( className );
		if ( cached != null ) {
			return cached;
		}

		try {
			final ClassInfo classInfo = indexer.indexClass( classReference );
			cachedInfoMap.put( className, classInfo );
			furtherProcess( classInfo );
			return classInfo;
		}
		catch (IOException e) {
			throw new HibernateException( "Jandex was unable to index Class reference - " + classReference, e );
		}
	}

	private ClassInfo indexClass(String className) {

		final String resourceName = className.replace( '.', '/' ) + ".class";
		final URL resourceUrl = resourceLocator.locateResource( resourceName );
		if ( resourceUrl == null ) {
			throw new HibernateException( "Could not locate .class file for Class [" + className + "] via resource lookup" );
		}

		try {
			try ( InputStream stream = resourceUrl.openStream() ) {
				try {
					return indexer.index( stream );
				}
				catch (IOException e) {
					throw new HibernateException(
							String.format(
									Locale.ROOT,
									"Unable to index Class [%s] from resource stream [%s]",
									className,
									resourceUrl.toExternalForm()
							),
							e
					);
				}
			}
		}
		catch (IOException e) {
			throw new HibernateException( "Unable to open InputStream for .class file : " + resourceUrl.toExternalForm() );
		}
	}

	private void furtherProcess(ClassInfo classInfo) {
		if ( classInfo.superName() != null ) {
			indexClass( classInfo.superName().toString() );
		}

		for ( DotName interfaceDotName : classInfo.interfaceNames() ) {
			indexClass( interfaceDotName.toString() );
		}

		// todo : nested classes do not seem to be available from Jandex ClassInfo

		final List<AnnotationInstance> entityListenerAnnotations = classInfo.annotations().get( SpecDotNames.ENTITY_LISTENERS );
		if ( entityListenerAnnotations != null ) {
			for ( AnnotationInstance entityListenerAnnotation : entityListenerAnnotations ) {
				final Type[] entityListenerClassTypes = entityListenerAnnotation.value().asClassArray();
				for ( Type entityListenerClassType : entityListenerClassTypes ) {
					indexClass( entityListenerClassType.name().toString() );
				}
			}
		}

		// todo : others?
	}
}
