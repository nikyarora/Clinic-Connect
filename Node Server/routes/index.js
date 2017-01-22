var express = require('express');
var router = express.Router();
var fs = require('fs');
var path = require("path");

router.get('/', function(req, res) {
    //return locations with populatuon
    
    var obj = JSON.parse(fs.readFileSync(path.resolve(__dirname, '../public/json/main.json'), 'utf8'));
    res.json(obj);
    
    
});

router.get('/add_data/:lat/:long', function(req, res) {
    
   var lat = req.params.lat;
   var long = req.params.long;
    
    
    
   var obj = JSON.parse(fs.readFileSync(path.resolve(__dirname, '../public/json/main.json'), 'utf8'));
    
   var didChange = false;
    
   for (var index in obj) {
        if (obj[index].lat == lat &&  obj[index].long == long) {
            didChange = true;
            obj[index].population = obj[index].population + 1;
            fs.writeFileSync(path.resolve(__dirname, '../public/json/main.json'), JSON.stringify(obj));
        }
    }
    
    if (didChange == false) {
        //append
        obj[Object.keys(obj).length] = {"lat": lat, "long": long, "population": 1};
        fs.writeFileSync(path.resolve(__dirname, '../public/json/main.json'), JSON.stringify(obj));
        
    }
    
    res.json(obj);
    
    
    
});

router.get('/decrease_data/:lat/:long', function(req, res) {
    
   var lat = req.params.lat;
   var long = req.params.long;
    
    
    
   var obj = JSON.parse(fs.readFileSync(path.resolve(__dirname, '../public/json/main.json'), 'utf8'));
    
   var didChange = false;
    
   for (var index in obj) {
        if (obj[index].lat == lat &&  obj[index].long == long) {
            didChange = true;
            obj[index].population = obj[index].population - 1;
            fs.writeFileSync(path.resolve(__dirname, '../public/json/main.json'), JSON.stringify(obj));
        }
    }
    
    
    res.json(obj);
    
    
    
});





module.exports = router;