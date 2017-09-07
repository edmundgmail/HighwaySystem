package com.ddp.lrs.models

/**
  * Created by vagrant on 9/7/17.
  */
import java.lang.reflect.Type

import scala.collection.mutable
import com.google.gson._

/**
  * Created by vagrant on 8/18/17.
  */

class DataRecordDeserializer extends JsonDeserializer[DataRecord]
{
  private var dataTypeRegistry: mutable.Map[String, Class[_ <: DataRecord]] = mutable.HashMap.empty

  def registerDataType(action: String, dataRecordInstanceClass: Class[_ <: DataRecord]): Unit = {
    dataTypeRegistry.put(action, dataRecordInstanceClass)
  }

  @throws[JsonParseException]
  def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DataRecord = {
    val jsonObject = json.getAsJsonObject
    val dataType = dataTypeRegistry.get(jsonObject.get("action").getAsString)
    if (!dataType.isEmpty) {
      val ret = context.deserialize[DataRecord](jsonObject, dataType.get)
      ret
    }
    else
      throw new Exception("Can't parse ")
  }

}

object DataRecordDeserializer{
  private var instance : DataRecordDeserializer = null

  def getInstance : DataRecordDeserializer = {
    if(instance ==null){
      instance = new DataRecordDeserializer
      instance.registerDataType(AddRoadRecord.getClass.getSimpleName.dropRight(1), classOf[AddRoadRecord])
      instance.registerDataType(AddSegmentRecord.getClass.getSimpleName.dropRight(1), classOf[AddSegmentRecord])
      instance.registerDataType(RemoveSegmentRecord.getClass.getSimpleName.dropRight(1), classOf[RemoveSegmentRecord])
    }
    instance
  }
}
