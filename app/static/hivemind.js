
let localClients = [];

function update(){
    fetch("http://localhost/clients").then((response) => {
        return response.json();
    }).then((remoteClient) => {
        if(remoteClient.length <= 0){
            document.getElementById("container").innerHTML = "No clients found.";
            return;
        }
        for(let i = 0; i < remoteClient.length; i++){
            if(!localClients[remoteClient[i].playerID]){
                createClient(remoteClient[i])
            }else{
                updateClient(localClients[remoteClient[i].playerID], remoteClient[i]);
                renderClient(localClients[remoteClient[i].playerID]);
            }
        }
    });
}

function renderClient(localClient) {
    localClient.ctx.drawImage(localClient.mapImage, 0, 0, localClient.canvas.width, localClient.canvas.height);
    localClient.ctx.fillStyle = "red";
    localClient.ctx.fillRect((localClient.x/100)*localClient.canvas.width, (localClient.y/100)*localClient.canvas.height, 10, 10);
    console.log(localClient.x, localClient.y)
}

function createClient(remoteClient) {
    console.log("Creating new client: ", remoteClient);
    localClients[remoteClient.playerID] = remoteClient;
    
    localClients[remoteClient.playerID].canvas = document.createElement("canvas");
    localClients[remoteClient.playerID].canvas.width = 480;
    localClients[remoteClient.playerID].canvas.height = 300;
    localClients[remoteClient.playerID].ctx = localClients[remoteClient.playerID].canvas.getContext("2d");

    localClients[remoteClient.playerID].mapImage = new Image();
    localClients[remoteClient.playerID].mapImage.src = "http://localhost/maps/" + localClients[remoteClient.playerID].mapID + ".png";

    console.log("Data: " + localClients[remoteClient.playerID].mapImage.src);

    document.getElementById("container").appendChild(localClients[remoteClient.playerID].canvas);
}

function updateClient(localClient, remoteClient) {
    if(localClient.mapID != remoteClient.mapID){
        localClient.mapID = remoteClient.mapID;
        localClient.mapImage.src = "http://localhost/maps/" + localClients[remoteClient.playerID].mapID  + ".png";
    }
    localClient.x = remoteClient.x;
    localClient.y = remoteClient.y;
    localClient.face = remoteClient.face;
    localClient.health = remoteClient.health;
    localClient.mana = remoteClient.mana;
    localClient.xp = remoteClient.xp;
    localClient.level = remoteClient.level;
}

setInterval(function() {
    update();
}, 200);