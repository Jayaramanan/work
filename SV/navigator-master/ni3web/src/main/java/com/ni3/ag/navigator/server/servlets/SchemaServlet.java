/** * Copyright (c) 2009-2015 Social Vision GmbH. All rights reserved. */
package com.ni3.ag.navigator.server.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ni3.ag.adminconsole.domain.UserActivityType;
import com.ni3.ag.navigator.server.NSpringFactory;
import com.ni3.ag.navigator.server.cache.UserGroupCache;
import com.ni3.ag.navigator.server.dao.ObjectConnectionDAO;
import com.ni3.ag.navigator.server.dao.SchemaDAO;
import com.ni3.ag.navigator.server.domain.Attribute;
import com.ni3.ag.navigator.server.domain.AttributeGroup;
import com.ni3.ag.navigator.server.domain.Context;
import com.ni3.ag.navigator.server.domain.ObjectConnection;
import com.ni3.ag.navigator.server.domain.ObjectDefinition;
import com.ni3.ag.navigator.server.domain.ObjectDefinitionGroup;
import com.ni3.ag.navigator.server.domain.PredefinedAttribute;
import com.ni3.ag.navigator.server.domain.Schema;
import com.ni3.ag.navigator.server.services.MetaphorService;
import com.ni3.ag.navigator.server.services.PrefilterService;
import com.ni3.ag.navigator.server.services.SchemaLoaderService;
import com.ni3.ag.navigator.server.services.TranslationService;
import com.ni3.ag.navigator.server.services.VisibilityService;
import com.ni3.ag.navigator.server.session.ThreadLocalStorage;
import com.ni3.ag.navigator.shared.domain.Prefilter;
import com.ni3.ag.navigator.shared.domain.UrlOperation;
import com.ni3.ag.navigator.shared.proto.NRequest;
import com.ni3.ag.navigator.shared.proto.NResponse;
import com.ni3.ag.navigator.shared.proto.NResponse.Envelope.Builder;

public class SchemaServlet extends Ni3Servlet{

	private static final Logger log = Logger.getLogger(SchemaServlet.class);
	private static final long serialVersionUID = 1L;

	public SchemaServlet(){
		log.info("Ni3 Application server - SchemaLoader v3.3 build 0001");
	}

	@Override
	public void destroy(){
		super.destroy(); // Just puts "destroy" string in log
	}

	@Override
	protected void doInternalPost(final HttpServletRequest request, final HttpServletResponse response)
			throws ServletException, IOException{
		final NRequest.Schema schemaRequest = NRequest.Schema.parseFrom(getInputStream(request));
		NResponse.Envelope.Builder resultBuilder = NResponse.Envelope.newBuilder();
		switch (schemaRequest.getAction()){
			case GET_SCHEMA_DATA:
				performGetSchemaData(schemaRequest, resultBuilder);
				break;
			case GET_SCHEMAS:
				performGetSchemas(resultBuilder);
				break;
			case GET_CONNECTIONS:
				performGetConnections(schemaRequest, resultBuilder);
				break;
			case GET_METAPHOR_SETS:
				performGetMetaphorSets(schemaRequest, resultBuilder);
				break;
			case GET_PREFILTER_DATA:
				performGetPrefilterData(schemaRequest, resultBuilder);
				break;
		}

		resultBuilder.setStatus(NResponse.Envelope.Status.SUCCESS);
		sendResponse(request, response, resultBuilder);
		log.debug("Loading schema completed");
	}

	private void performGetSchemaData(NRequest.Schema schemaRequest, Builder resultBuilder){
		final SchemaLoaderService slService = NSpringFactory.getInstance().getSchemaLoaderService();
		final VisibilityService visibilityService = NSpringFactory.getInstance().getVisibilityService();
		final TranslationService trlService = NSpringFactory.getInstance().getTranslationService();

		final ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		final UserGroupCache userGroupCache = NSpringFactory.getInstance().getUserGroupCache();
		final int groupId = userGroupCache.getGroup(storage.getCurrentUser().getId());

		final Schema initialSchema = slService.getSchema(schemaRequest.getSchemaId());
		log.debug("Loading schema " + initialSchema.getName());

		Schema schema = visibilityService.getSchemaWithPrivileges(initialSchema, groupId);
		schema = trlService.translateSchema(schema, schemaRequest.getLanguageId());

		final NResponse.Schema.Builder schemaResponse = NResponse.Schema.newBuilder();
		schemaResponse.setId(schema.getId());
		final List<NResponse.Entity> entityBuilders = fillEntities(schema.getDefinitions(), groupId);
		schemaResponse.addAllEntities(entityBuilders);

		resultBuilder.setPayload(schemaResponse.build().toByteString());
	}

	private void performGetSchemas(Builder resultBuilder){
		final SchemaDAO schemaDAO = NSpringFactory.getInstance().getSchemaDAO();
		List<Schema> schemas = schemaDAO.getSchemas();
		NResponse.Schemas.Builder schemasBuilder = NResponse.Schemas.newBuilder();
		for (Schema sch : schemas){
			schemasBuilder.addSchemas(NResponse.Schema.newBuilder().setId(sch.getId()).setName(sch.getName()));
		}
		resultBuilder.setPayload(schemasBuilder.build().toByteString());
	}

	private void performGetConnections(NRequest.Schema schemaRequest, Builder resultBuilder){
		final int schemaId = schemaRequest.getSchemaId();

		final ObjectConnectionDAO objectConnectionDAO = NSpringFactory.getInstance().getObjectConnectionDAO();
		List<ObjectConnection> objectConnections = objectConnectionDAO.getObjectConnections(schemaId);
		NResponse.ObjectConnections.Builder ocBuilder = NResponse.ObjectConnections.newBuilder();
		for (ObjectConnection oc : objectConnections){
			ocBuilder.addObjectConnections(NResponse.ObjectConnection.newBuilder().setFromObject(oc.getFromObject().getId())
					.setToObject(oc.getToObject().getId()).setConnectionObject(oc.getConnectionObject().getId())
					.setConnectionType(oc.getConnectionType()).setLineStyle(oc.getLineStyle().toInt()).setLineWidth(
							oc.getLineWidth()).setColor(oc.getColor()));
		}
		resultBuilder.setPayload(ocBuilder.build().toByteString());
	}

	private void performGetMetaphorSets(NRequest.Schema schemaRequest, Builder resultBuilder){
		final int schemaId = schemaRequest.getSchemaId();
		MetaphorService metaphorService = NSpringFactory.getInstance().getMetaphorService();
		List<String> sets = metaphorService.getMetaphorSets(schemaId);
		NResponse.MetaphorSets.Builder setsBuilder = NResponse.MetaphorSets.newBuilder();
		for (String s : sets)
			setsBuilder.addMetaphorSets(s);
		resultBuilder.setPayload(setsBuilder.build().toByteString());
	}

	private boolean performGetPrefilterData(NRequest.Schema schemaRequest, Builder resultBuilder){
		final int schemaId = schemaRequest.getSchemaId();
		final ThreadLocalStorage storage = NSpringFactory.getInstance().getThreadLocalStorage();
		final UserGroupCache userGroupCache = NSpringFactory.getInstance().getUserGroupCache();
		final int groupId = userGroupCache.getGroup(storage.getCurrentUser().getId());

		PrefilterService prefilterService = NSpringFactory.getInstance().getPrefilterService();
		List<Prefilter> pfList = prefilterService.getPrefilter(groupId, schemaId);
		NResponse.Prefilter.Builder protoPfBuilder = NResponse.Prefilter.newBuilder();
		for (Prefilter pf : pfList){
			protoPfBuilder.addItem(NResponse.PrefilterItem.newBuilder().setId(pf.getId()).setGroupId(pf.getGroupId())
					.setSchemaId(pf.getSchemaId()).setObjectDefinitionId(pf.getObjectDefinitionId()).setAttributeId(
							pf.getAttributeId()).setPredefinedId(pf.getPredefinedId()));
		}
		resultBuilder.setPayload(protoPfBuilder.build().toByteString());
		return true;
	}

	private List<NResponse.Entity> fillEntities(List<ObjectDefinition> entities, int groupId){
		final List<NResponse.Entity> protoEntities = new ArrayList<NResponse.Entity>();
		log.debug("Fill object definitions, count = " + entities.size());
		for (ObjectDefinition entity : entities){
			ObjectDefinitionGroup odg = getPermissionForGroup(entity, groupId);
			final NResponse.Entity.Builder entityBuilder = NResponse.Entity.newBuilder();
			entityBuilder.setId(entity.getId());
			entityBuilder.setName(entity.getName());
			entityBuilder.setObjectTypeId(entity.getObjectTypeId());
			entityBuilder.setSort(entity.getSort());
			if (entity.getDescription() != null)
				entityBuilder.setDescription(entity.getDescription());
			entityBuilder.setCanRead(odg.isCanRead());
			entityBuilder.setCanCreate(odg.isCanCreate());
			entityBuilder.setCanUpdate(odg.isCanUpdate());
			entityBuilder.setCanDelete(odg.isCanDelete());

			final List<UrlOperation> urls = entity.getUrlOperations();
			for (UrlOperation url : urls){
				NResponse.UrlOperation.Builder urlBuilder = NResponse.UrlOperation.newBuilder();
				urlBuilder.setId(url.getId());
				urlBuilder.setLabel(url.getLabel());
				urlBuilder.setUrl(url.getUrl());
				urlBuilder.setSort(url.getSort());
				entityBuilder.addUrlOperations(urlBuilder);
			}

			log.debug("Fill attributes for entity " + entity.getName() + ", count = " + entity.getAttributes().size());
			List<NResponse.Attribute> protoAttributes = fillAttributes(groupId, entity.getAttributes());

			entityBuilder.addAllAttributes(protoAttributes);

			final List<Context> contexts = entity.getContexts();
			List<NResponse.Context> protoContexts = fillContexts(contexts);
			entityBuilder.addAllContexts(protoContexts);

			protoEntities.add(entityBuilder.build());
		}
		return protoEntities;
	}

	private ObjectDefinitionGroup getPermissionForGroup(ObjectDefinition entity, int groupId){
		for (ObjectDefinitionGroup odg : entity.getObjectPermissions()){
			if (odg.getGroupId() == groupId)
				return odg;
		}
		return null;
	}

	private List<NResponse.Attribute> fillAttributes(int groupId, List<Attribute> attributes){
		final List<NResponse.Attribute> protoAttributes = new ArrayList<NResponse.Attribute>();
		for (Attribute attribute : attributes){
			AttributeGroup attributeGroup = getAttributeGroup(attribute.getAttributeGroups(), groupId);
			NResponse.Attribute.Builder b = NResponse.Attribute.newBuilder();
			b.setId(attribute.getId());
			b.setName(attribute.getName());
			b.setLabel(attribute.getLabel());
			final String description = attribute.getDescription();
			if (description != null){
				b.setDescription(description);
			}
			b.setPredefined(attribute.isPredefined());
			b.setFormula(attribute.isFormula());
			b.setInFilter(attribute.isInFilter());
			b.setInLabel(attribute.isInLabel());
			b.setInToolTip(attribute.isInToolTip());
			b.setInAdvancedSearch(attribute.isInAdvancedSearch());
			b.setInSimpleSearch(attribute.isInSimpleSearch());
			b.setInMatrix(attribute.getInMatrix());
			b.setInMetaphor(attribute.isInMetaphor());
			b.setInExport(attribute.isInExport());
			b.setInPrefilter(attribute.isInPrefilter());
			b.setInContext(attribute.isInContext());
			b.setDataTypeId(attribute.getDataType().toInt());
			b.setSort(attribute.getSort());
			b.setSortLabel(attribute.getSortLabel());
			b.setSortFilter(attribute.getSortFilter());
			b.setSortSearch(attribute.getSortSearch());
			b.setSortMatrix(attribute.getSortMatrix());
			b.setLabelBold(attribute.isLabelBold());
			b.setLabelItalic(attribute.isLabelItalic());
			b.setLabelUnderline(attribute.isLabelUnderline());
			b.setContentBold(attribute.isContentBold());
			b.setContentItalic(attribute.isContentItalic());
			b.setContentUnderline(attribute.isContentUnderline());
			final String format = attribute.getFormat();
			if (format != null){
				b.setFormat(format);
			}
			final String editFormat = attribute.getEditFormat();
			if (editFormat != null){
				b.setEditFormat(editFormat);
			}
			final String validCharacters = attribute.getValidCharacters();
			if (validCharacters != null){
				b.setValidCharacters(validCharacters);
			}
			final String invalidCharacters = attribute.getInvalidCharacters();
			if (invalidCharacters != null){
				b.setInvalidCharacters(invalidCharacters);
			}
			final String minVal = attribute.getMinValue();
			if (minVal != null){
				b.setMinValue(minVal);
			}
			final String maxVal = attribute.getMaxValue();
			if (maxVal != null){
				b.setMaxValue(maxVal);
			}
			final String regexp = attribute.getRegExpression();
			if (regexp != null){
				b.setRegularExpression(regexp);
			}
			final String valueDescription = attribute.getValueDescription();
			if (valueDescription != null){
				b.setValueDescription(valueDescription);
			}
			b.setMultivalue(attribute.isMultivalue());
			b.setAggregable(attribute.isAggregable());

			b.setCanRead(attributeGroup.getCanRead());
			b.setEditLock(NResponse.Attribute.EditOption.valueOf(attribute.getEditLocked()));
			b.setEditUnlock(NResponse.Attribute.EditOption.valueOf(attribute.getEditUnlocked()));

			if (attribute.isPredefined()){
				final List<NResponse.AttributeValue> values = fillPredefinedValues(attribute);
				b.addAllValues(values);
			}

			protoAttributes.add(b.build());
		}
		return protoAttributes;
	}

	private AttributeGroup getAttributeGroup(List<AttributeGroup> attributeGroups, int groupId){
		for (AttributeGroup ag : attributeGroups){
			if (ag.getGroupId() == groupId)
				return ag;
		}
		return null;
	}

	@Override
	protected UserActivityType getActivityType(){
		return null;
	}

	@Override
	protected List<LogParam> getActivityParams(){
		return null;
	}

	private List<NResponse.Context> fillContexts(List<Context> contexts){
		final List<NResponse.Context> ret = new ArrayList<NResponse.Context>();
		for (final com.ni3.ag.navigator.server.domain.Context context : contexts){
			final com.ni3.ag.navigator.shared.proto.NResponse.Context.Builder builder = NResponse.Context.newBuilder();

			builder.setId(context.getId());
			builder.setName(context.getName());
			builder.setPkAttributeId(context.getPkAttribute().getId());

			final List<Attribute> attributes = context.getAttributes();
			for (Attribute attribute : attributes){
				builder.addRelatedAttributes(attribute.getId());
			}
			ret.add(builder.build());
		}
		return ret;
	}

	private List<NResponse.AttributeValue> fillPredefinedValues(final Attribute a){
		final List<NResponse.AttributeValue> values = new ArrayList<NResponse.AttributeValue>();

		for (PredefinedAttribute attribute : a.getValues()){
			final NResponse.AttributeValue.Builder builder = NResponse.AttributeValue.newBuilder();
			builder.setId(attribute.getId());
			builder.setParentId(attribute.getParent() != null ? attribute.getParent().getId() : 0);
			builder.setLabel(attribute.getLabelTrl());
			builder.setSort(attribute.getSort());
			builder.setValue(attribute.getValue());
			builder.setToUse(attribute.getTouse() != null && attribute.getTouse() == 1);
			final String haloColor = attribute.getHaloColor();
			if (haloColor != null){
				builder.setHaloColor(haloColor);
			}
			builder.setHaloColorSelected(false);
			values.add(builder.build());
		}

		return values;
	}

}
