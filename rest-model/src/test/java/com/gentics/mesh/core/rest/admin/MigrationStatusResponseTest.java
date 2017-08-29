package com.gentics.mesh.core.rest.admin;

import static com.gentics.mesh.core.rest.admin.MigrationStatus.COMPLETED;
import static com.gentics.mesh.core.rest.admin.MigrationStatus.FAILED;
import static com.gentics.mesh.core.rest.admin.MigrationStatus.IDLE;
import static com.gentics.mesh.core.rest.admin.MigrationStatus.STARTING;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MigrationStatusResponseTest {

	@Test
	public void testStatusHandling() {
		MigrationStatusResponse response = new MigrationStatusResponse();
		response.getMigrations().add(new MigrationInfo().setStartDate("2017-08-28T13:13:44Z").setStatus(COMPLETED));
		response.getMigrations().add(new MigrationInfo().setStartDate("2017-08-28T13:13:46Z").setStatus(STARTING));
		response.getMigrations().add(new MigrationInfo().setStartDate("2017-08-28T13:13:45Z").setStatus(COMPLETED));
		assertEquals(STARTING, response.getStatus());

		response = new MigrationStatusResponse();
		response.getMigrations().add(new MigrationInfo().setStartDate("2017-08-28T13:13:44Z").setStatus(COMPLETED));
		response.getMigrations().add(new MigrationInfo().setStartDate("2017-08-28T13:13:46Z").setStatus(FAILED));
		response.getMigrations().add(new MigrationInfo().setStartDate("2017-08-28T13:13:45Z").setStatus(COMPLETED));
		assertEquals(IDLE, response.getStatus());
	}
}