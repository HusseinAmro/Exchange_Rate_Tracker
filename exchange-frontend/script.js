var addButton = document.getElementById("add-button");
var LBPAmount = document.getElementById("lbp-amount");
var USDAmount = document.getElementById("usd-amount");
var buyRate = document.getElementById("buy-usd-rate");
var sellRate = document.getElementById("sell-usd-rate");
var transactionType = document.getElementById("transaction-type");
var SERVER_URL = "http://127.0.0.1:5000";

addButton.addEventListener("click", addItem);

function addItem(event) {
    event.preventDefault();
    var x = LBPAmount.value;
    var y = USDAmount.value;
    if (x == "" || y == "") {
      alert("Please fill the required fields");
    } else if (x <= 0 || y <= 0) {
      alert("Please enter an appropriate input");
      LBPAmount.value = "";
      USDAmount.value = "";
    } else {
        var trans;
        if (transactionType.value === "usd-to-lbp"){
            trans = 1;
        }
        else{
            trans = 0;
        }
        var transactionData = {
            lbp_amount: Number(x),
            usd_amount: Number(y),
            usd_to_lbp: trans,
        };
        fetch(SERVER_URL + "/transaction", {
            method: "POST",
            headers: {
            "Content-Type": "application/json",
            },
            body: JSON.stringify(transactionData),
        })
            .then((response) => response.json())
            .then((data) => {
            console.log("Transaction added:", data);
            fetchRates();
            })
            .catch((error) => {
            console.error("Error adding transaction:", error);
            });
        
        LBPAmount.value = "";
        USDAmount.value = "";
    }
}

function fetchRates() {
    fetch(`${SERVER_URL}/exchangeRate`)
    .then(response => response.json())
    .then(data => {
        sellRate.innerHTML = data.usd_to_lbp.toFixed(2);
        buyRate.innerHTML = data.lbp_to_usd.toFixed(2);
    });
}
fetchRates();