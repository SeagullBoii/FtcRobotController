const API_BASE = window.location.origin;

const modal = document.getElementById("mode-modal");
const autoBtn = document.getElementById("auto-btn");
const teleopBtn = document.getElementById("teleop-btn");
const classSelect = document.getElementById("class-select");
const arenaImage = document.getElementById('arena-image');
const coordDisplay = document.getElementById('coord-display');
const autoModal = document.querySelector(".auto-modal");
const teleopModal = document.querySelector(".teleop-modal");
const stateButton = document.querySelector(".state-button");
const unitButton = document.querySelector(".unit-button");
const unitsSelect = document.getElementById("units");
const refreshButton = document.querySelector(".refresh-button");

const ARENA_WIDTH_INCHES = 72;
const ARENA_HEIGHT_INCHES = 72;

const robotDimensions = {
  width: 12,
  length: 12
}

const data = {
  currentX: 36,
  currentY: 36,
  currentHeading: 0
}

const classes = [
    {
        class: "test123",
        fields: [
            {
                var: "testvar123",
                val: 67.420
            }
        ]
    },
    {
        class: "testtest",
        fields: [
            {
                var: "sigma",
                val: "muieee"
            },
            {
                var: "hehe",
                val: 12345
            }
        ]
    }
];

const modes = {
  teleop: ["test", "sigma"],
  auto: ["Auto Far Rosu", "Auto Close Rosu", "Auto Far Albastru", "Auto Close Albastru"]
}

let currentModeType = "", currentMode = "";
let opmodeTitle = document.querySelector(".opmode-title");
let currentState = 0;
let currentUnit = "inches";

stateButton.disabled = true;

function setState(state) {
  const button = document.querySelector(".state-button");

  switch (state) {
      case 0:
        button.textContent = "INIT";
        currentState = 0;
        break;
      case 1:
        button.textContent = "START";
        currentState = 1;
        break;
      case 2:
        button.textContent = "STOP";
        currentState = 2;
        break;
    }
}

function toggleState() {
  stateButton.addEventListener("click", () => {
    if (!checkAvailable()) return;

    currentState = (currentState + 1) % 3;
    setState(currentState);
  });

}

function checkAvailable() {
  const options = document.querySelectorAll(".modal-option");

  for(const option of options) {
    if(option.classList.contains("selected")) {
      return true;
    }
  }

  return false;
}

function convertValue(value, fromUnit, toUnit) {
  if (fromUnit === toUnit) return value;
  if (fromUnit === "inches" && toUnit === "centimeters") return value * 2.54;
  if (fromUnit === "centimeters" && toUnit === "inches") return value / 2.54;
}

function getUnitSymbol(unit) {
  return unit === "inches" ? '"' : "cm";
}

function sendDataToModal() {
  const autoModes = modes.auto;
  const autoModal = document.querySelector(".auto-modal");

  autoModes.forEach((mode) => {
    const autoOption = document.createElement("button");
    
    autoOption.classList.add("modal-option");
    autoOption.textContent = mode;

    autoModal.appendChild(autoOption);
  });

  const teleopModes = modes.teleop;
  const teleopModal = document.querySelector(".teleop-modal");

  teleopModes.forEach((mode) => {
    const teleopOption = document.createElement("button");

    teleopOption.classList.add("modal-option");
    teleopOption.textContent = mode;

    teleopModal.appendChild(teleopOption);
  });
}

function updateConnectionStatus(status) {
  document.getElementById("connection-status").textContent = status;
}

function updatePingTime(time) {
  document.getElementById("ping-time").textContent = time;
}

function updateTelemetry(key, value) {
  const element = document.getElementById(`telemetry-${key}`);
  if (element) {
    element.textContent = `${key}: ${value}`;
  }
}

function updateArenaData(data) {
  const unit = getUnitSymbol(currentUnit);
  
  if (data.startX !== undefined) {
    const convertedX = convertValue(data.startX, "inches", currentUnit);
    document.getElementById("start-x").textContent = `startX: ${convertedX.toFixed(2)}${unit}`;
  }
  
  if (data.startY !== undefined) {
    const convertedY = convertValue(data.startY, "inches", currentUnit);
    document.getElementById("start-y").textContent = `startY: ${convertedY.toFixed(2)}${unit}`;
  }
  
  if (data.startHeading !== undefined)
    document.getElementById("start-heading").textContent = `startHeading: ${data.startHeading.toFixed(2)}°`;
  
  if (data.currentX !== undefined) {
    const convertedX = convertValue(data.currentX, "inches", currentUnit);
    document.getElementById("current-x").textContent = `currentX: ${convertedX.toFixed(2)}${unit}`;
  }
  
  if (data.currentY !== undefined) {
    const convertedY = convertValue(data.currentY, "inches", currentUnit);
    document.getElementById("current-y").textContent = `currentY: ${convertedY.toFixed(2)}${unit}`;
  }
  
  if (data.currentHeading !== undefined)
    document.getElementById("current-heading").textContent = `currentHeading: ${data.currentHeading.toFixed(2)}°`;
}

function updateTestVar(value) {
  document.getElementById("test-var").textContent = value.toFixed(2);
}

function updateBattery(voltage) {
  const batteryFill = document.getElementById("battery-fill");
  const batteryVoltage = document.getElementById("battery-voltage");

  const minVoltage = 10;
  const maxVoltage = 13;
  const percentage = Math.max(
    0,
    Math.min(100, ((voltage - minVoltage) / (maxVoltage - minVoltage)) * 100),
  );

  batteryFill.style.width = percentage + "%";
  batteryVoltage.textContent = voltage.toFixed(1) + "V";

  if (percentage > 60) {
    batteryFill.style.backgroundColor = "#4ade80";
  } else if (percentage > 30) {
    batteryFill.style.backgroundColor = "#fbbf24";
  } else {
    batteryFill.style.backgroundColor = "#ef4444";
  }
}

async function updateBatteryVoltage() {
  try {
    const response = await fetch(`${API_BASE}/api/batteryVoltage`);

    if (!response.ok) {
      throw new Error(`HTTP ${response.status}`);
    }

    const { voltage } = await response.json();

    document.getElementById("battery-voltage").textContent =
      typeof voltage === "number" ? `${voltage.toFixed(3)} V` : "N/A";

    updateBattery(voltage);
  } catch (err) {
    console.error("Failed to fetch battery voltage:", err);
    document.getElementById("battery-voltage").textContent = "Error";
    document.getElementById("error").textContent = err.message;
  }
}

async function fetchTelemetry() {
  try {
    const res = await fetch("/api/telemetry");
    if (!res.ok) return;

    const data = await res.json();
    renderTelemetry(data);
  } catch (e) {
    console.error("Telemetry fetch failed", e);
  }
}

function renderTelemetry(telemetry) {
  const container = document.getElementById('telemetry-content');
  container.textContent = '';

  for (const [key, entry] of Object.entries(telemetry)) {
    const p = document.createElement('p');
    
    if (entry.isLine) p.textContent = entry.value;
    else p.textContent = `${entry.caption}: ${entry.value}`;
      p.className = 'telemetry-item';
    
    container.appendChild(p);
  }
}

async function sendPing() {
    const startTime = Date.now();

    try {
        const response = await fetch(`/api/latency?ts=${startTime}`);
        const data = await response.json();

        const rtt = Date.now() - startTime;

        updatePingTime(`${rtt} ms`);
    } catch (e) {
        console.error("Ping failed", e);
    }
}


function createOptions() {
    const defaultOption = document.createElement("option");
    classSelect.add(defaultOption);
    defaultOption.value = "default";
    defaultOption.text = "--Select a Class--";

    classes.forEach((classItem) => {
        const option = document.createElement("option");
        classSelect.add(option);
        option.value = classItem.class;
        option.text = `${classItem.class}`;
    });
}

function createVariables() {
    const className = document.getElementById("class-select").value;

    const classFields = classes.find(c => c.class === className).fields;

    const main = document.querySelector(".configurables");
    const container = document.createElement("div");
    main.appendChild(container);
    container.className = "classContainer";

    classFields.forEach((field) => {
        const fieldDiv = document.createElement("div");
        fieldDiv.className = "classVars";

        const label = document.createElement("p");
        label.textContent = field.var;

        const input = document.createElement("input");

        if(typeof field.val === "number") {
            input.type = "number";
            input.value = field.val;
        } else {
            input.type = "text";
            input.value = field.val;
        }

        fieldDiv.appendChild(label);
        fieldDiv.appendChild(input);
        
        container.appendChild(fieldDiv);    
    });
}

function updatePingTime(time) { 
  document.getElementById("ping-time").textContent = time; 
}

function inchesToPixelsX(inches) {
  return (inches / ARENA_WIDTH_INCHES) * arenaImage.clientWidth;
}

function inchesToPixelsY(inches) {
  return (inches / ARENA_HEIGHT_INCHES) * arenaImage.clientHeight;
}

function inchesToCentimeters(inches) {
  return inches * 2.54;
}


function createRobotShape() {
  const robotShape = document.querySelector(".robot-shape");

  const pxWidth  = inchesToPixelsX(robotDimensions.width);
  const pxLength = inchesToPixelsY(robotDimensions.length);

  const pxX = inchesToPixelsX(data.currentX);
  const pxY = inchesToPixelsY(data.currentY);

  robotShape.style.width  = `${pxWidth}px`;
  robotShape.style.height = `${pxLength}px`;

  robotShape.style.left = `${pxX - pxWidth / 2}px`;
  robotShape.style.bottom = `${pxY - pxLength / 2}px`;


  robotShape.style.transform = `rotate(${data.currentHeading}deg)`;

  arenaImage.appendChild(robotShape);
}

autoBtn.addEventListener("click", () => {
  currentModeType = "auto";
  autoModal.style.display = "block";
  teleopModal.style.display = "none";
  modal.style.display = "flex";
});

teleopBtn.addEventListener("click", () => {
  currentModeType = "teleop";
  teleopModal.style.display = "block";
  autoModal.style.display = "none";
  modal.style.display = "flex";
});

modal.addEventListener("click", (e) => {
  if (e.target.classList.contains("modal-option")) {

    document.querySelectorAll(".modal-option").forEach((opt) => opt.classList.remove("selected"));
    e.target.classList.add("selected");

    const newMode = e.target.textContent;
    
    if (currentMode != newMode) {
      currentState = 0;
      setState(0);
    }

    currentMode = newMode;
    opmodeTitle.textContent = currentMode;

    stateButton.disabled = false;
    modal.style.display = "none";
  }
  
  if (e.target === modal) {
    modal.style.display = "none";
  }
});

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape' && modal.style.display === 'flex') {
    modal.style.display = 'none';
  }
});

classSelect.addEventListener("change", () => {
    const classVars = document.querySelectorAll(".classVars");
    const classContainers = document.querySelectorAll(".classContainer");

    if(classContainers.length >= 2) {
      classContainers[0].remove();
    }

    classVars.forEach((varDiv) => varDiv.remove());
    
    createVariables();
});

arenaImage.addEventListener('mousemove', (e) => {
    coordDisplay.style.display = "initial";
    const rect = arenaImage.getBoundingClientRect();

    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    const flippedY = rect.height - y;

    const xInches = (x / rect.width) * ARENA_WIDTH_INCHES;
    const yInches = (flippedY / rect.height) * ARENA_HEIGHT_INCHES;
    
    const xConverted = convertValue(xInches, "inches", currentUnit);
    const yConverted = convertValue(yInches, "inches", currentUnit);
    const unit = getUnitSymbol(currentUnit);
    
    coordDisplay.textContent = `X: ${xConverted.toFixed(2)}${unit}, Y: ${yConverted.toFixed(2)}${unit}`;
});

unitsSelect.addEventListener("change", (e) => {
    currentUnit = e.target.value;
    updateArenaData(data);
    createRobotShape();
});

arenaImage.addEventListener('mouseleave', () => {
    coordDisplay.textContent = 'X: 0, Y: 0';
    coordDisplay.style.display = "none";
});

refreshButton.addEventListener("click", () => {

});

async function getAllClasses() {
    const response = await fetch('/api/configurableVariables');
    const classes = await response.json();
    return classes;
}

async function getClass(className) {
    const response = await fetch(`/api/configurableVariables?class=${encodeURIComponent(className)}`);
    
    if (!response.ok) {
        throw new Error(`Class not found: ${className}`);
    }
    
    const classes = await response.json();
    return classes[0];
}
async function getAllClasses() {
    const response = await fetch('/api/configurableVariables');
    
    if (!response.ok) {
        throw new Error(`Failed to fetch classes: ${response.status}`);
    }
    
    const classes = await response.json();
    return classes;
}

async function getClass(className) {
    const response = await fetch(`/api/configurableVariables?class=${encodeURIComponent(className)}`);
    
    if (!response.ok) {
        throw new Error(`Class not found: ${className}`);
    }
    
    const classes = await response.json();
    return classes[0];
}
addEventListener("DOMContentLoaded", async () => {
    try {
        console.log("Fetching from /api/configurableVariables");
        
        const response = await fetch('/api/configurableVariables');
        console.log("Response status:", response.status);
        console.log("Response ok:", response.ok);
        console.log("Response type:", response.type);
        
        const text = await response.text();
        console.log("Raw response text:", text);
        console.log("Text length:", text.length);
        
        if (!text || text === "null" || text.trim() === "") {
            console.error("Empty or null response");
            return;
        }
        
        const fetchedClasses = JSON.parse(text);
        console.log("Parsed classes:", fetchedClasses);
        
        if (Array.isArray(fetchedClasses) && fetchedClasses.length > 0) {
            classes.length = 0; 
            Array.prototype.push.apply(classes, fetchedClasses);
            console.log(`Successfully loaded ${classes.length} classes`);
        }
    } catch (error) {
        console.error("Error:", error);
    }
});
sendDataToModal();
updateBatteryVoltage();
fetchTelemetry();
sendPing();
createOptions();
updateArenaData(data);
setState(currentState);
toggleState();
createRobotShape();

setInterval(updateBatteryVoltage, 1000);
setInterval(fetchTelemetry, 100);
setInterval(sendPing, 1000);