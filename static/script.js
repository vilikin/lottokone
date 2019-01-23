const LOTTO_API_URL = getCurrentBaseUrl() + "/api/row";

let maxTicks = 100;
let tickIntervalMillis = 100;
let tickCounter = 1;

function getCurrentBaseUrl() {
    const url = new URL(window.location.href);
    return url.origin;
}

// Returns array of random lotto numbers
async function fetchLottoNumbers() {
    const result = await fetch(LOTTO_API_URL);
    return await result.json();
}

async function initiateLottery() {
    const apiRow = await fetchLottoNumbers();

    const tickInterval = setInterval(() => {
        tick(apiRow)
    }, tickIntervalMillis);

    setTimeout(
        () => {
            clearInterval(tickInterval);
            tickCounter = 1;
        },
        maxTicks * tickIntervalMillis
    );
}

function tick(apiRow) {
    const lockedNumbersCount = Math.round(tickCounter / maxTicks * 7);
    const unlockedNumbersCount = 7 - lockedNumbersCount;

    const lockedNumbers = apiRow.slice(0, lockedNumbersCount);
    const unlockedNumbers = createArrayOfRandomNumbers(unlockedNumbersCount);

    const currentRow = [...lockedNumbers, ...unlockedNumbers];

    renderRow(currentRow);

    tickCounter++;
}

function renderRow(row) {
    document.getElementById("lotto-row")
        .innerText = row.map(padWithZero).join(" • ");
}

function padWithZero(number) {
    const numberAsString = number.toString();
    return numberAsString.length > 1
        ? numberAsString
        : "0" + numberAsString;
}

function createArrayOfRandomNumbers(arrayLength) {
    const array = [];

    for (let i = 0; i < arrayLength; i++) {
        array.push(Math.ceil(Math.random() * 38))
    }

    return array;
}
