<#-- @ftlvariable name="folder" type="com.paul.prototype.model.Asset" -->
{
  "name" : "${asset.jcrNodeName}",
  "primaryType" : "hippogallery:exampleAssetSet",
  "mixinTypes" : [ ],
  "properties" : [ {
    "name" : "jcr:path",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${asset.jcrPath}" ]
  }, {
    "name" : "jcr:localizedName",
    "type" : "STRING",
    "multiple" : false,
    "values" : [ "${asset.localizedName}" ]
  } ],
  "nodes" : [ {
    "name" : "hippogallery:asset",
    "primaryType" : "hippo:resource",
    "mixinTypes" : [ ],
    "properties" : [ {
      "name" : "jcr:data",
      "type" : "BINARY",
      "multiple" : false,
      "values" : [ "${asset.filePath}" ]
    }, {
      "name" : "jcr:lastModified",
      "type" : "DATE",
      "multiple" : false,
      "values" : [ "${asset.lastModifiedDate}" ]
    }, {
      "name" : "jcr:mimeType",
      "type" : "STRING",
      "multiple" : false,
      "values" : [ "${asset.mimeType}" ]
    } ],
    "nodes" : [ ]
  } ]
  }
