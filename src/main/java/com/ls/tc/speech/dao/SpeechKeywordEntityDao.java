package com.ls.tc.speech.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ls.tc.speech.dao.entity.SpeechKeywordEntity;
import com.ls.tc.speech.dao.entity.SpeechKeywordEntity.SpeechKeywordEntityPK;

public interface SpeechKeywordEntityDao extends JpaRepository<SpeechKeywordEntity, SpeechKeywordEntityPK> {

	List<SpeechKeywordEntity> findByIdSpeechId(String speechId);

}
