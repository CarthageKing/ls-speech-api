package com.ls.tc.speech.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ls.tc.speech.dao.entity.SpeechAuthorEntity;
import com.ls.tc.speech.dao.entity.SpeechAuthorEntity.SpeechAuthorityEntityPK;

public interface SpeechAuthorityEntityDao extends JpaRepository<SpeechAuthorEntity, SpeechAuthorityEntityPK> {

	List<SpeechAuthorEntity> findByIdSpeechId(String speechId);
}
