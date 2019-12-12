package com.f2m.aquarius.parameters;

public class ECMParameters {

	// System User:
	public static final String ECM_SYSTEM = "ECM_SYSTEM";
	public static final String TEST_SYSTEM = "TEST_SYSTEM";
	
	// Storage Area:
	public static final String SA_STATUS_ACTIVE = "active";
	public static final String SA_STATUS_STANDBY = "standby";
	public static final String SA_STATUS_CLOSE = "closed";
	public static final String SA_PREFIX = "prefix";
	public static final String SA_DEFAULT_PATH = "defaultPath";
	
	// Storage Policy:
	public static final String SP_TYPE_DOMAIN = "domain";
	public static final String SP_TYPE_FOLDER = "folder";
	public static final String SP_TYPE_CLASS = "class";
	public static final String SP_TYPE = "type";
	public static final String SP_PREFIX = "prefix";
	
	// Node:
	public static final String N_TYPE_DOMAIN = "domain";
	public static final String N_TYPE_FOLDER = "folder";
	public static final String N_TYPE_DOCUMENT = "document";
	public static final String N_STATUS_ACTIVE = "active";
	public static final String N_STATUS_DELETED = "deleted";
	public static final String N_STATUS_CLOSE = "closed";
	public static final String N_FIELD_DOMAIN = "domain";
	public static final String N_FIELD_ID = "id";
	
	// Document
	public static final String D_V_STATUS_ACTIVE = "active";
	public static final String D_V_STATUS_CLOSE = "closed";
	public static final String D_D_STATUS_ACTIVE = "active";
	public static final String D_D_STATUS_CLOSE = "closed";
	public static final String D_FIELD_TITLE = "title";
	
	// Folder Paramss:
	public static final String FOLDER_VISIBILITY = "visible";
	
	// Busqueda:
	public static final String SEARCH_TYPE_FOLDER = "folder";
	public static final String SEARCH_TYPE_DOCUMENT = "document";
	public static final String SEARCH_OPERATOR_AND = "and";
	public static final String SEARCH_OPERATOR_OR = "or";
	
	//thumbnail:
	public static final String THUMB_DEFAULT = "default";
	public static final String THUMB_PATH = "thumbnail";
	public static final String THUMB_MIME_TYPE = "image/png";
	
	//Download Folder:
	public static final String DOWNLOAD_FOLDER_CONTENT = "content";
	public static final String DOWNLOAD_FOLDER_METADATA = "metadata";
	public static final String DOWNLOAD_FOLDER_CONTENT_METADATA = "contentMetadata";
	public static final String DOWNLOAD_FOLDER_LIST = "list";
	
	// Download Document:
	public static final String DOWNLOAD_DOCUMENT_ORIGINAL = "original";
	public static final String DOWNLOAD_DOCUMENT_ZIP_CONTENT = "zipContent";
	public static final String DOWNLOAD_DOCUMENT_ZIP_CONTENT_METADATA = "zipContentMetadata";
	public static final String DOWNLOAD_DOCUMENT_METADATA_CSV = "csv";
	public static final String DOWNLOAD_DOCUMENT_METADATA_PDF = "pdf";
	
	
	// Templates: Folder
	public static final String TEMPLATE_FOLDER = "folder";
	public static final String TEMPLATE_DOCUMENT = "document";
}
