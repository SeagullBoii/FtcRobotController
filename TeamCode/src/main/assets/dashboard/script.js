const API_BASE = window.location.origin;

// Modal handling
const modal = document.getElementById("mode-modal");
const autoBtn = document.getElementById("auto-btn");
const teleopBtn = document.getElementById("teleop-btn");
const modalTitle = document.getElementById("modal-title");
const modalOptions = document.querySelectorAll(".modal-option");
const classSelect = document.getElementById("class-select");
const arenaImage = document.getElementById('arena-image');
const coordDisplay = document.getElementById('coord-display');

const ARENA_WIDTH_INCHES = 72;
const ARENA_HEIGHT_INCHES = 72;

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

let currentModeType = "";

autoBtn.addEventListener("click", () => {
  currentModeType = "auto";
  modalTitle.textContent = "Select Auto Mode";
  modal.style.display = "flex";
});

teleopBtn.addEventListener("click", () => {
  currentModeType = "teleop";
  modalTitle.textContent = "Select Teleop Mode";
  modal.style.display = "flex";
});

modalOptions.forEach((option) => {
  option.addEventListener("click", (e) => {
    modalOptions.forEach((opt) => opt.classList.remove("selected"));
    e.target.classList.add("selected");
    setTimeout(() => {
      modal.style.display = "none";
    }, 300);
  });
});

modal.addEventListener("click", (e) => {
  if (e.target === modal) {
    modal.style.display = "none";
  }
});

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
  if (data.startOffsetX !== undefined)
    document.getElementById("offset-x").textContent = `startOffsetX: ${data.startOffsetX}"`;
  
  if (data.startOffsetY !== undefined)
    document.getElementById("offset-y").textContent = `startOffsetY: ${data.startOffsetY}"`;
  
  if (data.startHeading !== undefined)
    document.getElementById("start-heading").textContent = `startHeading: ${data.startHeading}"`;
  
  if (data.currentX !== undefined)
    document.getElementById("current-x").textContent = `currentX: ${data.currentX}"`;
  
  if (data.currentY !== undefined)
    document.getElementById("current-y").textContent = `currentY: ${data.currentY}"`;
  
  if (data.currentHeading !== undefined)
    document.getElementById("current-heading").textContent = `currentHeading: ${data.currentHeading}°`;
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
  container.innerHTML = '';

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
        
        console.log("Actual Latency (ms):", rtt);
        updatePingTime(rtt + " ms");
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

function updatePingTime(time) { document.getElementById("ping-time").textContent = time; }

updateBatteryVoltage();
fetchTelemetry();
sendPing();
createOptions();

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
    
    coordDisplay.textContent = `X: ${xInches.toFixed(2)}", Y: ${yInches.toFixed(2)}"`;
});

arenaImage.addEventListener('mouseleave', () => {
    coordDisplay.textContent = 'X: 0, Y: 0';
    coordDisplay.style.display = "none";
});

setInterval(updateBatteryVoltage, 1000);
setInterval(fetchTelemetry, 100);
setInterval(sendPing, 1000);
