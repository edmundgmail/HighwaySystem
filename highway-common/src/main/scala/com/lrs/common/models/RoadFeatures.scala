package com.lrs.common.models

/**
  * Created by vagrant on 10/18/17.
  */
case class RoadFeatures(roadId: Long, dir: String, segments: List[Segment],
                        COG: String,
                        county: String,
                        engineerDistrict: String,
                        cutAndGutter: String,
                        cutAndGutterLeftOrCard: Boolean
                       )
