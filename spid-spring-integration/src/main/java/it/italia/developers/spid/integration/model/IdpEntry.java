package it.italia.developers.spid.integration.model;

public class IdpEntry {

	private String identifier;
	private String entityId;
	private String name;

	public IdpEntry() {
	}

	public IdpEntry(final String identifier, final String entityId, final String name) {
		this.identifier = identifier;
		this.entityId = entityId;
		this.name = name;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(final String entityId) {
		this.entityId = entityId;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
