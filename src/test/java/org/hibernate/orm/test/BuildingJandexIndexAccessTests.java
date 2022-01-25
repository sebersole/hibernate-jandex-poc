package org.hibernate.orm.test;

import org.hibernate.orm.boot.jandex.internal.BuildingJandexIndexAccess;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class BuildingJandexIndexAccessTests {
	@Test
	public void simpleTest() {
		final Indexer indexer = new Indexer();
		final BuildingJandexIndexAccess indexAccess = new BuildingJandexIndexAccess(
				indexer,
				(resourceName) -> getClass().getClassLoader().getResource( resourceName )
		);

		final ClassInfo classInfo = indexAccess.classInfo( AnnotatedEntity.class );

		final ClassInfo classInfo2 = indexAccess.classInfo( AnnotatedEntity.class );
		assertThat( classInfo2 ).isSameAs( classInfo );

		final Index index = indexer.complete();

		assertThat( index.getClassByName( classInfo.name() ) ).isSameAs( classInfo );
		// id, name
		assertThat( classInfo.fields() ).hasSize( 2 );
		// 2 constructors, getter for id, getter/setter for name
		assertThat( classInfo.methods() ).hasSize( 5 );
	}

	@Test
	public void nonAnnotatedTest() {
		final Indexer indexer = new Indexer();
		final BuildingJandexIndexAccess indexAccess = new BuildingJandexIndexAccess(
				indexer,
				(resourceName) -> getClass().getClassLoader().getResource( resourceName )
		);

		final ClassInfo classInfo = indexAccess.classInfo( NonAnnotatedEntity.class );

		final ClassInfo classInfo2 = indexAccess.classInfo( NonAnnotatedEntity.class );
		assertThat( classInfo2 ).isSameAs( classInfo );

		// id, name
		assertThat( classInfo.fields() ).hasSize( 2 );
		// 2 constructors, getter for id, getter/setter for name
		assertThat( classInfo.methods() ).hasSize( 5 );
	}
}
