package org.hibernate.orm.boot.spi;

import java.net.URL;

/**
 * Access to load resources during bootstrap
 *
 * @author Steve Ebersole
 */
@FunctionalInterface
public interface ResourceLocator {
	/**
	 * Locate a resource by name
	 *
	 * @see ClassLoader#getResource(String)
	 */
	URL locateResource(String resourceName);
}
