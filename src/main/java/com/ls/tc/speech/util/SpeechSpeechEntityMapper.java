package com.ls.tc.speech.util;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ls.tc.speech.controller.model.Speech;
import com.ls.tc.speech.dao.entity.SpeechEntity;

@Mapper
public interface SpeechSpeechEntityMapper {

	SpeechSpeechEntityMapper INSTANCE = Mappers.getMapper(SpeechSpeechEntityMapper.class);

	SpeechEntity speechToSpeechEntity(Speech speech);

	Speech speechEntityToSpeech(SpeechEntity speechEntity);
}
