
// The local clients array stores remote client data on the front-end.
// Always operate from local client data.
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
    localClient.ctx.beginPath();
    localClient.ctx.arc(((localClient.x/100)*localClient.canvas.width), ((localClient.y/100)*localClient.canvas.height), 4, 0, 2 * Math.PI);
    localClient.ctx.fill();

    console.log(localClient.orders);
    if(localClient.orders.length > 0){
        localClient.ctx.fillStyle = "red";
        localClient.ctx.beginPath();
        localClient.ctx.moveTo(localClient.orders[0].x * localClient.canvas.width, localClient.orders[0].y * localClient.canvas.height);
        for(let i = 1; i < localClient.orders.length; i++) {
            localClient.ctx.lineTo(localClient.orders[i].x * localClient.canvas.width, localClient.orders[i].y * localClient.canvas.height);
        }
        localClient.ctx.stroke();
    }

}

function createClient(remoteClient) {
    console.log("Creating new client: ", remoteClient);
    localClients[remoteClient.playerID] = remoteClient;
    
    localClients[remoteClient.playerID].canvas = document.createElement("canvas");
    localClients[remoteClient.playerID].canvas.width = 480;
    localClients[remoteClient.playerID].canvas.height = 300;
    localClients[remoteClient.playerID].canvas.addEventListener("click", (e)=>{handleClientClick(e, localClients[remoteClient.playerID])});
    localClients[remoteClient.playerID].ctx = localClients[remoteClient.playerID].canvas.getContext("2d");

    localClients[remoteClient.playerID].mapImage = new Image();
    localClients[remoteClient.playerID].mapImage.src = "http://localhost/maps/" + localClients[remoteClient.playerID].mapID + ".png";

    localClients[remoteClient.playerID].orders = [];
    
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

function handleClientClick(e, remoteClient) {
    console.log("Pressed:", e, " ", remoteClient);
    const xPos = e.layerX / localClients[remoteClient.playerID].canvas.width;
    const yPos = e.layerY / localClients[remoteClient.playerID].canvas.height;
    if(e.shiftKey) {
        localClients[remoteClient.playerID].orders.push({
            type: "move",
            x: xPos,
            y: yPos
        });
    }else{
        localClients[remoteClient.playerID].orders = [{
            type: "move",
            x: xPos,
            y: yPos
        }];
    }
}

setInterval(function() {
    update();
}, 200);