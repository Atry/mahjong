package me.yingrui.segment.core

import me.yingrui.segment.conf.SegmentConfiguration
import me.yingrui.segment.dict.DictionaryService
import me.yingrui.segment.filter.SegmentResultFilter

class MPSegmentWorker(config: SegmentConfiguration, unKnownFilter: SegmentResultFilter, dictionaryService: DictionaryService) extends SegmentWorker {

  def this(config: SegmentConfiguration, dictionaryService: DictionaryService) = this(config, new SegmentResultFilter(config), dictionaryService)

  private val maxSegStrLength = 400000
  private val mpSegment: MPSegment = new MPSegment(config, dictionaryService)
  private val recognizePOS = config.isRecognizePOS()

  def segment(sen: String): SegmentResult = {
    var sentence = sen
    var result: SegmentResult = null
    if (sentence != null && sentence.length() > 0) {
      if (sentence.length() > maxSegStrLength) {
        sentence = sentence.substring(0, maxSegStrLength)
      }
      result = mpSegment.segmentMP(sentence, recognizePOS)
      if (recognizePOS) {
        unKnownFilter.filter(result)
      }
    } else {
      result = new SegmentResult(0)
    }
    result
  }

  def tokenize(sen: String): Array[String] = segment(sen).getWords().map(_.name)
}
