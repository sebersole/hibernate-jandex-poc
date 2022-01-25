package org.hibernate.orm.boot.jandex.internal;

import org.jboss.jandex.DotName;

import jakarta.persistence.EntityListeners;

/**
 * {@link DotName} references for {@link jakarta.persistence} types.
 *
 * @author Steve Ebersole
 */
public interface SpecDotNames {
	DotName ENTITY_LISTENERS = DotName.createSimple( EntityListeners.class.getName() );
}
