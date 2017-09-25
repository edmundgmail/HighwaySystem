

download Robo3T
docker pull library/mongo
docker run -p 127.0.0.1:27017:27017 --name road -d mongo

docker network create mynet
sudo docker run -p 0.0.0.0:27017:27017 --net=mynet --name roadmongo -d mongo

sudo docker run -p 0.0.0.0:5000:5000 --net=mynet --name highwy-rest -d  ddp/highway-rest:0e2286ec97016e3a04c6687859df9fca8327d402-SNAPSHOT
~


# HighwaySystem

{
  "action": "AddRoadRecord",
  "roadName": "Hero High Way",
  "roadId" : "1",
  "dateTime" : "20170101 01:10:10",
  "mainDir": "E",
   "jurisDictionType" : "jurisDictionType",
   "ownerShip" : "ownerShip"
   "prefixCode" : "prefixCode",
   "routeNumber":"routeNumber",
   "modifierCode":"modifierCode",
   "mainlineCode":"mainlineCode",
   "routeTypeCode":"routeTypeCode",
   "routeOfficialName":"routeOfficialName",
   "routeFullName":"routeFullName",
   "routeAlternateName":"routeAlternateName",
   "beginPlace":"beginPlace",
   "endPlace":"endPlace",
  "directions": [
    {
      "dir": "E",
      "segments" : ["1.8,RP1,2.1,RP2,1.0","1.9,RP3,2.3,RP4,0.9,RP5,0.5"]
    },
    {
      "dir": "W",
      "segments": ["2.9,RP1,1.1,RP2,3.0","2.9,RP3,2.5,RP4,0.9,RP5,0.6"]
    }
  ]
}
