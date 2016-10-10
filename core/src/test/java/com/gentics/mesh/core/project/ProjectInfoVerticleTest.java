package com.gentics.mesh.core.project;

import static com.gentics.mesh.demo.TestDataProvider.PROJECT_NAME;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.gentics.mesh.core.rest.project.ProjectResponse;
import com.gentics.mesh.test.AbstractIsolatedRestVerticleTest;

public class ProjectInfoVerticleTest extends AbstractIsolatedRestVerticleTest {

	@Test
	public void testReadProjectByName() {
		ProjectResponse project = call(() -> getClient().findProjectByName(PROJECT_NAME));
		assertEquals(PROJECT_NAME, project.getName());
	}

}