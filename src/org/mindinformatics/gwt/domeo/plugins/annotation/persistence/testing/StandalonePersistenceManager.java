package org.mindinformatics.gwt.domeo.plugins.annotation.persistence.testing;

import java.util.List;

import org.mindinformatics.gwt.domeo.client.IDomeo;
import org.mindinformatics.gwt.domeo.plugins.annotation.persistence.src.APersistenceManager;
import org.mindinformatics.gwt.domeo.plugins.annotation.persistence.src.IPersistenceManager;
import org.mindinformatics.gwt.domeo.plugins.annotation.persistence.src.IRetrieveExistingAnnotationSetHandler;
import org.mindinformatics.gwt.domeo.plugins.annotation.persistence.src.IRetrieveExistingAnnotationSetListHandler;
import org.mindinformatics.gwt.domeo.plugins.annotation.persistence.src.IRetrieveExistingBibliographySetHandler;
import org.mindinformatics.gwt.framework.src.ICommandCompleted;

import com.google.gwt.user.client.Window;

public class StandalonePersistenceManager extends APersistenceManager implements IPersistenceManager {

	public StandalonePersistenceManager(IDomeo domeo, ICommandCompleted callback) {
		super(domeo, callback);
	}

	@Override
	public void saveAnnotation() {
		Window.alert("StandalonePersistenceManager.saveAnnotation()");
	}

	@Override
	public void saveBibliography() {
		Window.alert("StandalonePersistenceManager.saveBibliography()");
	}

	@Override
	public void retrieveExistingAnnotationSetList(final IRetrieveExistingAnnotationSetListHandler handler) {
		Window.alert("StandalonePersistenceManager.retrieveAnnotation()");
	}
	
	@Override
	public void retrieveExistingBibliographySet(
			IRetrieveExistingBibliographySetHandler handler) {
		Window.alert("StandalonePersistenceManager.retrieveExistingBibliographySet()");
	}

	@Override
	public void retrieveExistingAnnotationSets(List<String> ids,
			IRetrieveExistingAnnotationSetHandler handler) {
		Window.alert("StandalonePersistenceManager.retrieveExistingBibliographySet() for #" + ids.size());
	}
}
