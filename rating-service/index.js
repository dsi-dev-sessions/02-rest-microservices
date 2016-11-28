const express = require("express");
const app = express();
const jwt = require("jsonwebtoken");
const uuid = require("uuid");
const PORT = 3000;
const HOST = require('os').hostname();

var bodyParser = require('body-parser');
var consul = require('consul')({
    host: "service-discovery"
});

consul.agent.service.register({ 
          name : 'ratings-service', 
          id: uuid.v4(), 
          address: HOST,
          port:PORT,
          check : {
            http: `http://${HOST}:${PORT}/health`,
            interval: "15s"
          }
      }, function(err) {
  if (err) throw err;
});

app.use(bodyParser.json());

const neo4j = require("neo4j-driver").v1;

const driver = neo4j.driver("bolt://"+process.env['NEO4J_HOSTNAME']);

var checkAuth = (req, res, next) => {
    const authorization = req.headers.authorization;
    if (authorization && authorization.startsWith("Bearer ")) {
        const jwtToken = authorization.substr(7);
        jwt.verify(jwtToken, process.env['JWT_SECRET'], (err, decoded) => {
            req.principal = decoded.sub;
            next();
        });
    } else {
        res.status(401).send({"error": "Missing a valid authorization header"});
    }
}

app.get('/health', function(req, res) {
    res.send({'name' : 'ratings-service', 'status' : 'OK'});
});

app.get('/ratings/:movie', function (req, res) {
    const query = `
        MATCH (movie:MOVIE)<-[r:RATED]-(u:USER) WHERE movie.id = {movie}
        RETURN AVG(r.rating) as avgRating;
    `;
    const params = {
        movie: "" + req.params.movie
    };
    const session = driver.session();
    session.run(query, params).then((result) => {
        session.close();
        res.send({ averageRating: result.records[0]._fields[0] });
    }).catch((err) => {
        session.close();
        res.status(500).send({"error": "Could not compute rating average"});
    });
});



app.post('/ratings', checkAuth, function (req, res) {
    const query = `
        MERGE (movie:MOVIE { id: {movie} })
        MERGE (user:USER { username: {username} })
        MERGE (movie)<-[r:RATED]-(user)
        SET r.rating = {rating}
    `;
    const params = {
        rating: req.body.rating,
        movie: "" + req.body.movie,
        username: req.principal
    };
    const session = driver.session();
    session.run(query, params).then((result) => {
        session.close();
        res.status(201).send(req.body);
    }).
    catch((err) => {
        console.log(err);
        session.close();
        res.status(500).send({"error": "Could not save rating"});
    });
});

// session.run(query, params).then((result) => {
//
// }).catch((error) => {
//
// });

const populate = function(params) {
    const query = `
            MERGE (movie:MOVIE { id: {movie} })
            MERGE (user:USER { username: {username} })
            MERGE (movie)<-[r:RATED]-(user)
            SET r.rating = {rating}
        `;
    const session = driver.session();
    session.run(query, params).then((result) => {
        session.close();
    }).
    catch((err) => {
        session.close();
    });
};

populate({
    movie : 1,
    username : "user5",
    rating: 1
});

populate({
    movie : 2,
    username : "user5",
    rating: 2
});

populate({
    movie : 3,
    username : "user5",
    rating: 3
});

app.listen(PORT, function () {
    console.log('Example app listening on port ' + PORT)
})