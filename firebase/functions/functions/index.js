

//====================================> Cloud Functions

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

const runtimeOpts = {
  timeoutSeconds: 540,
}

const cloudFunctionCreate = functions.runWith(runtimeOpts).https.onRequest((request, response) => {
    return requestAzanTimes().then(function(objects) {
        return addAzanTimesToDB(objects).then(function() {
              console.log(objects.length+ " Items Inserted");
              response.send(JSON.stringify(objects));
              return null
          });
    }).catch(err => {
        console.log(err);
        response.send(err)
    });
});

const cloudFunctionDelete = functions.runWith(runtimeOpts).https.onRequest(async (request, response) => {
    await deleteAzanTimesInDB();
    console.log("Items deleted");
    response.send("Items deleted");
});

const cloudFunctionGet = functions.runWith(runtimeOpts).https.onRequest(async (request, response) => {
  let dayOfMonth = request.query.day;
  let monthOfYear = request.query.month;
  let year = request.query.year;

  let snapshot;
  if(dayOfMonth && monthOfYear && year) {
    snapshot = await getAzanTimesSnapShotFromDBBy(dayOfMonth,monthOfYear,year);
  }else{
    snapshot  = await getAzanTimesSnapShotFromDB();
  }

  console.log("By: "+dayOfMonth+ ","+ monthOfYear+ ","+year+ " => Items Count: "+snapshot.size)
  response.send(JSON.stringify(snapshot.docs.map(doc => doc.data())));
});

const cloudFunctionCount = functions.runWith(runtimeOpts).https.onRequest(async (request, response) => {
    const snapshot = await getAzanTimesSnapShotFromDB();
    console.log("Items Count: "+snapshot.size);
    response.send("Items Count: "+snapshot.size);
});

// At 12:00:00pm, every 7 days starting on the 1st, every month
const cloudFunctionSyncJob = functions.runWith(runtimeOpts).pubsub.schedule('0 0 1 * *').timeZone('Europe/Berlin').onRun((context) => {
  console.log("Sync Started!");
  return requestAzanTimes().then(function(objects) {
      return addAzanTimesToDB(objects).then(function() {
            console.log(objects.length+ " Items Inserted");
            return null
        });
  }).catch(err => console.log(err));
})

exports.create = cloudFunctionCreate;

exports.delete = cloudFunctionDelete;

exports.get = cloudFunctionGet;

exports.count = cloudFunctionCount;

exports.syncjob = cloudFunctionSyncJob

//====================================> Azan Times Request & Process Logic

async function requestAzanTimes(){
    return new Promise((resolve, reject) => {
      // HTTPS
      const https = require('https')
      https.get('https://www.islamisches-zentrum-muenchen.de/wp-content/themes/IslamischeZentrum/js/custom.js', (resp) => {
        let data = '';
        // A chunk of data has been received.
        resp.on('data', (chunk) => {  data += chunk; });
        // The whole response has been received. Print out the result.
        resp.on('end', () => {
            try{
              if (resp.statusCode >= 200 && resp.statusCode <= 299) {
                const objects = processAzanTimes(data);
                resolve(objects);
              }else{
                reject('Request failed. status: ' + resp.statusCode + ', body: ' + data);
              }
            }catch(err){
              reject(err);
            }
        });
      }).on("error", (err) => { reject(err); });
    });
}

function processAzanTimes(data){
  const objects=[];
  const regex = /(M\d{0,2})\s*=\s*new\s*Array\s*\((\S*)\)/g;
  const matches = data.matchAll(regex);
  const year = new Date().getFullYear();
  const nextYear = year + 1;
  for (const match of matches) {
    const monthOfYear = parseInt(match[1].substring(1));
    const monthTimesInString = match[2];
    const monthTimesArray = monthTimesInString.split(",");
    for (var i = 0; i < monthTimesArray.length; i++) {
     const time = monthTimesArray[i].replace(/(^"|"$)/g, '');
     const dayOfMonth = Math.ceil((i+1)/6);
     /*
      * 0 -> Fajr
      * 1 -> Sunrise
      * 2 -> Dhuhr
      * 3 -> Asr
      * 4 -> Maghrib
      * 5 -> Isha
      */
      const azan = i % 6;
      // Current Year
      objects.push(
        {
          "id": dayOfMonth+"-"+monthOfYear+"-"+year+":"+azan,
          "dayOfMonth": dayOfMonth,
          "monthOfYear": monthOfYear,
          "year": year,
          "time": time,
          "azan": azan
        }
      );
      // Next year
      objects.push(
        {
          "id": dayOfMonth+"-"+monthOfYear+"-"+nextYear+":"+azan,
          "dayOfMonth": dayOfMonth,
          "monthOfYear": monthOfYear,
          "year": nextYear,
          "time": time,
          "azan": azan
        }
      );
    }
  }
  return objects;
}

//====================================> DB Operations

const collectionName = "azan-times";
const admin = require('firebase-admin');
admin.initializeApp();

const firestore = admin.firestore();

async function addAzanTimesToDB(objects){
  const chunkFunc = require('lodash/chunk');
  // Firebase limitation
  // Bulk Insert can add only max of 500 items per commit
  const batches = chunkFunc(objects, 500).map(objectsChunk => {
      const batch = firestore.batch();
      objectsChunk.forEach(object => {
          const doc = firestore.collection(collectionName).doc(object["id"]);
          batch.set(doc, object);
      })
      return batch.commit();
  })
  return Promise.all(batches);
}

async function deleteAzanTimesInDB(){
  const chunkFunc = require('lodash/chunk');
  const snapshot = await getAzanTimesSnapShotFromDB();
  const batches = chunkFunc(snapshot.docs, 500).map(docsChunk => {
      const batch = firestore.batch();
      docsChunk.forEach(doc => {
          batch.delete(doc.ref);
      })
      return batch.commit();
  })
  return Promise.all(batches);
}

async function getAzanTimesSnapShotFromDB(){
  return firestore.collection(collectionName).get();
}

async function getAzanTimesSnapShotFromDBBy(dayOfMonth,monthOfYear,year){
  let data = await firestore.collection(collectionName)
  .where('dayOfMonth', '==', parseInt(dayOfMonth))
  .where('monthOfYear', '==', parseInt(monthOfYear))
  .where('year', '==', parseInt(year))
  .orderBy("azan")
  .get()
  console.log("Data: "+ data.size);
  return data;
}

//====================================>
