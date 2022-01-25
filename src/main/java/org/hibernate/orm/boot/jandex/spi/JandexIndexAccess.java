package org.hibernate.orm.boot.jandex.spi;

import org.jboss.jandex.ClassInfo;

/**
 * @author Steve Ebersole
 */
public interface JandexIndexAccess {
	/**
	 * Information about a package.  The returned ClassInfo describes
	 * `package-info.class`
	 */
	ClassInfo packageInfo(String packageName);

	/**
	 * Information about a class
	 */
	ClassInfo classInfo(String className);

	/**
	 * Information about a class
	 */
	ClassInfo classInfo(Class<?> classReference);
}
