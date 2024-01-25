package com.ls.tc.speech.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.ls.tc.speech.dao.entity.SpeechAuthorEntity;
import com.ls.tc.speech.dao.entity.SpeechEntity;
import com.ls.tc.speech.dao.entity.SpeechKeywordEntity;
import com.ls.tc.speech.exception.SpeechAppValidationFailedException;
import com.ls.tc.speech.util.SpeechAppConstants;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;

@Repository
@Transactional
public class SpeechSearchEntityDao {

	@Resource
	private EntityManager entityMgr;

	public SpeechSearchEntityDao() {
		// noop
	}

	@SuppressWarnings("unchecked")
	public List<String> getIdsMatchingCriteria(String authors, String dateRangeFrom, String dateRangeTo, String keywords, String snippetsOfTexts) {
		List<String> authorsLst = tokenize(authors);
		List<String> keywordsLst = tokenize(keywords);
		List<String> snippetsOfTextLst = tokenize(snippetsOfTexts);
		Date fromDate = null;
		Date toDate = null;

		dateRangeFrom = StringUtils.trimToEmpty(dateRangeFrom);
		dateRangeTo = StringUtils.trimToEmpty(dateRangeTo);

		Set<ConstraintViolation<?>> errors = new HashSet<>();

		if (dateRangeFrom.length() > 0) {
			try {
				fromDate = Date.valueOf(LocalDate.parse(dateRangeFrom, SpeechAppConstants.STRICT_DATE_FORMATTER));
			} catch (Exception e) {
				errors.add(ConstraintViolationImpl.forParameterValidation("not a valid date", null, null, "not a valid date", null, null, null, null, PathImpl.createPathFromString("dateRangeFrom"), null, null, null));
			}
		}
		if (dateRangeTo.length() > 0) {
			try {
				toDate = Date.valueOf(LocalDate.parse(dateRangeTo, SpeechAppConstants.STRICT_DATE_FORMATTER));
			} catch (Exception e) {
				errors.add(ConstraintViolationImpl.forParameterValidation("not a valid date", null, null, "not a valid date", null, null, null, null, PathImpl.createPathFromString("dateRangeTo"), null, null, null));
			}
		}

		if (null != fromDate && null != toDate && fromDate.after(toDate)) {
			errors.add(ConstraintViolationImpl.forParameterValidation("must be equal or less than 'dateRangeTo'", null, null, "must be equal or less than 'dateRangeTo'", null, null, null, null, PathImpl.createPathFromString("dateRangeFrom"), null, null, null));
		}

		if (!CollectionUtils.isEmpty(errors)) {
			throw new SpeechAppValidationFailedException(errors);
		}

		StringBuilder sb = new StringBuilder("select a.id from ").append(SpeechEntity.class.getSimpleName()).append(" a ");
		Map<String, Object> parmMap = new HashMap<>();
		boolean first = true;

		if (null != fromDate) {
			sb.append(" where ");
			sb.append(" a.speechDate >= :fromDate ");
			parmMap.put("fromDate", fromDate);
			first = false;
		}

		if (null != toDate) {
			if (!first) {
				sb.append(" and ");
			} else {
				sb.append(" where ");
			}
			sb.append(" a.speechDate <= :toDate ");
			parmMap.put("toDate", toDate);
			first = false;
		}

		if (!CollectionUtils.isEmpty(snippetsOfTextLst)) {
			if (!first) {
				sb.append(" and ");
			} else {
				sb.append(" where ");
			}
			sb.append("(");
			for (int i = 0; i < snippetsOfTextLst.size(); i++) {
				String parmName = "sot" + i;
				if (i > 0) {
					sb.append(" or ");
				}
				sb.append("lower(a.speechText) like :" + parmName);
				parmMap.put(parmName, "%" + snippetsOfTextLst.get(i).toLowerCase() + "%");
			}
			sb.append(")");
			first = false;
		}

		if (!CollectionUtils.isEmpty(authorsLst)) {
			if (!first) {
				sb.append(" and ");
			} else {
				sb.append(" where ");
			}
			sb.append("(");
			sb.append("a.id in (select b.id.speechId from ").append(SpeechAuthorEntity.class.getSimpleName()).append(" b where (");
			for (int i = 0; i < authorsLst.size(); i++) {
				String parmName = "aut" + i;
				if (i > 0) {
					sb.append(" or ");
				}
				sb.append("lower(b.id.author) like :" + parmName);
				parmMap.put(parmName, "%" + authorsLst.get(i).toLowerCase() + "%");
			}
			sb.append(")))");
			first = false;
		}

		if (!CollectionUtils.isEmpty(keywordsLst)) {
			if (!first) {
				sb.append(" and ");
			} else {
				sb.append(" where ");
			}
			sb.append("(");
			sb.append("a.id in (select b.id.speechId from ").append(SpeechKeywordEntity.class.getSimpleName()).append(" b where (");
			for (int i = 0; i < keywordsLst.size(); i++) {
				String parmName = "keyw" + i;
				if (i > 0) {
					sb.append(" or ");
				}
				sb.append("lower(b.id.keyword) like :" + parmName);
				parmMap.put(parmName, "%" + keywordsLst.get(i).toLowerCase() + "%");
			}
			sb.append(")))");
			first = false;
		}

		sb.append(" order by a.speechDate, a.id");

		Query query = entityMgr.createQuery(sb.toString());
		for (Entry<String, Object> ent : parmMap.entrySet()) {
			query.setParameter(ent.getKey(), ent.getValue());
		}

		return query.getResultList();
	}

	private List<String> tokenize(String str) {
		str = StringUtils.trimToEmpty(str);

		List<String> lst = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(str, SpeechAppConstants.SEARCH_PARAM_MULTIVALUE_SEPARATOR);
		while (st.hasMoreTokens()) {
			String tok = StringUtils.trimToEmpty(st.nextToken());
			if (tok.length() > 0) {
				lst.add(tok);
			}
		}

		return lst;
	}
}
