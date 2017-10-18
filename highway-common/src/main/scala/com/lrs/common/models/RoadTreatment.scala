package com.lrs.common.models

/**
  * Created by vagrant on 10/18/17.
  */

case class TreatmentDetail(layerNo: Int, material: String, materialDesign: String, thickness: Double)
case class Treatment(desc: String, details: List[TreatmentDetail])

class RoadTreatment{
  var map: Map[Lane, Treatment] = _

  
}