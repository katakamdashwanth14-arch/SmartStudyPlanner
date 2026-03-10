import { sanitizeHTML, PRIORITY_COLORS, hexToCSS } from './utils.js';

let tasks = [];
let notes = [];
let reminders = [];
let timeBlocks = { morning: [], afternoon: [], evening: [], night: [] };

class Item {
    constructor(id, title) {
        this.id = id;
        this.title = title;
    }
}
class Task extends Item {
    constructor(id, subject, desc, priority, date, time, durationMinutes) {
        super(id, subject);
        this.desc = desc;
        this.priority = priority;
        this.date = date;
        this.time = time || "12:00";
        this.durationMinutes = durationMinutes || 60;
        this.completed = false;
    }
}


const playSound = () => {
    try {
        const audio = document.getElementById('pomodoro-alarm');
        if (audio) audio.play().catch(e => console.warn("Audio play blocked", e));
    } catch (e) { console.error("Audio error", e); }
};

document.addEventListener('DOMContentLoaded', () => {
    try {
        function loadData() {
            const uName = localStorage.getItem('username');
            const uDisplay = document.getElementById('user-display-name');
            if (uName && uDisplay) uDisplay.textContent = uName;

            const uWelcome = document.getElementById('welcome-user-name');
            if (uName && uWelcome) uWelcome.textContent = uName;


            try {
                const rawTasks = JSON.parse(localStorage.getItem('tasks'));
                tasks = Array.isArray(rawTasks) ? rawTasks : [];

                const rawNotes = JSON.parse(localStorage.getItem('notes'));
                notes = Array.isArray(rawNotes) ? rawNotes : [];

                const rawReminders = JSON.parse(localStorage.getItem('reminders'));
                reminders = Array.isArray(rawReminders) ? rawReminders : [];

                const tb = JSON.parse(localStorage.getItem('timeBlocks'));
                if (tb && typeof tb === 'object' && !Array.isArray(tb)) {
                    timeBlocks = {
                        morning: Array.isArray(tb.morning) ? tb.morning : [],
                        afternoon: Array.isArray(tb.afternoon) ? tb.afternoon : [],
                        evening: Array.isArray(tb.evening) ? tb.evening : [],
                        night: Array.isArray(tb.night) ? tb.night : []
                    };
                } else {
                    timeBlocks = { morning: [], afternoon: [], evening: [], night: [] };
                }
            } catch (err) {
                console.error("Data parse error", err);
                tasks = []; notes = []; reminders = [];
                timeBlocks = { morning: [], afternoon: [], evening: [], night: [] };
            }


            // Check which page we are on before rendering
            if (document.getElementById('task-list')) renderTasks();
            if (document.getElementById('notes-grid')) renderNotes();
            if (document.getElementById('reminder-list')) renderReminders();
            if (document.getElementById('morning-schedule')) renderSchedule();
            if (document.getElementById('progress-bar')) updateProgress();
            if (document.getElementById('quote-content')) fetchQuote();

            // Calendar init
            if (document.getElementById('calendar')) {
                const calendarEl = document.getElementById('calendar');
                window.calendar = new FullCalendar.Calendar(calendarEl, {
                    initialView: 'dayGridMonth',
                    height: 'auto',
                    headerToolbar: {
                        left: 'prev,next today',
                        center: 'title',
                        right: 'dayGridMonth,timeGridWeek,timeGridDay'
                    },
                    events: function (info, successCallback, failureCallback) {
                        const evt = tasks.map(t => {
                            let colorStr = t.completed ? '#28a745' : ((PRIORITY_COLORS && PRIORITY_COLORS[t.priority]) || '#007bff');
                            if (colorStr && !colorStr.startsWith('#')) colorStr = hexToCSS(colorStr);

                            return {
                                id: t.id,
                                title: t.title || t.subject,
                                start: t.date + 'T' + (t.time || '12:00') + ':00',
                                backgroundColor: colorStr,
                                borderColor: colorStr,
                                textColor: '#ffffff'
                            };
                        });
                        successCallback(evt);
                    },
                    eventClick: function (info) {
                        if (confirm(`Do you want to toggle completion for: ${info.event.title}?`)) {
                            window.toggleTask(parseInt(info.event.id));
                        }
                    }
                });
                window.calendar.render();
            }

            // Always render reminders counter
            const rC = document.getElementById('reminder-counter');
            if (rC) rC.textContent = reminders.length;

            try {
                if ("Notification" in window && Notification.permission !== "granted" && Notification.permission !== "denied") {
                    Notification.requestPermission().catch(e => console.warn(e));
                }
            } catch (e) { }

            checkOverdueTasks();
        }

        function checkOverdueTasks() {
            const now = new Date();
            const currentDate = now.toISOString().split('T')[0];
            const currentTime = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;

            let modifiedLists = false;
            tasks.forEach(t => {
                if (!t.completed) {
                    const taskDateObj = new Date(t.date);
                    const todayObj = new Date(currentDate);

                    const isPreviousDay = taskDateObj < todayObj;
                    const isTodayOverdue = (t.date === currentDate) && (currentTime >= "23:00");

                    // Check if strictly overdue or end of day today
                    if (isPreviousDay || isTodayOverdue) {
                        if (t.lastRemindedDate !== currentDate) {
                            t.lastRemindedDate = currentDate;

                            let reminderTitle = `DAILY REMINDER: Incomplete ${t.subject || t.title}`;

                            reminders.push({
                                id: Date.now() + Math.floor(Math.random() * 100000),
                                title: reminderTitle,
                                date: currentDate,
                                time: currentTime,
                                notified: false
                            });

                            modifiedLists = true;
                        }
                    }
                }
            });

            if (modifiedLists) {
                saveTasks();
                saveReminders();
                if (document.getElementById('task-list')) renderTasks();
                if (document.getElementById('reminder-list')) renderReminders();

                // Show notification for overdue tasks
                try {
                    if ("Notification" in window && Notification.permission === "granted") {
                        new Notification("Smart Planner", { body: "You have overdue tasks. Check your reminders!" });
                    }
                } catch (e) { }
            }
        }

        // Auth check
        if (localStorage.getItem('isLoggedIn') !== 'true') {
            window.location.href = 'index.html';
            return;
        }

        // Global Load
        // loadData() moved to the end of block

        // Logout
        const logoutBtn = document.getElementById('logout-btn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', (e) => {
                e.preventDefault();
                localStorage.removeItem('isLoggedIn');

                // Only drop name cache if 'remember' is not checked
                if (localStorage.getItem('rememberToken') !== 'true') {
                    localStorage.removeItem('username');
                }

                window.location.replace('index.html');
            });
        }

        // Theme Switch
        const themeSwitch = document.getElementById('checkbox');
        if (themeSwitch) {
            const savedTheme = localStorage.getItem('theme') || 'light';
            if (savedTheme === 'dark') {
                document.body.setAttribute('data-theme', 'dark');
                themeSwitch.checked = true;
            }

            themeSwitch.addEventListener('change', (e) => {
                if (e.target.checked) {
                    document.body.setAttribute('data-theme', 'dark');
                    localStorage.setItem('theme', 'dark');
                } else {
                    document.body.setAttribute('data-theme', 'light');
                    localStorage.setItem('theme', 'light');
                }
            });
        }

        // Task Page Logic
        const taskForm = document.getElementById('task-form');
        if (taskForm) {
            taskForm.addEventListener('submit', (e) => {
                e.preventDefault();
                const newTask = new Task(
                    Date.now(),
                    document.getElementById('task-subject').value,
                    document.getElementById('task-desc').value,
                    document.getElementById('task-priority').value,
                    document.getElementById('task-date').value
                );
                tasks.push(newTask);
                saveTasks();
                renderTasks();
                if (window.calendar) window.calendar.refetchEvents();
                taskForm.reset();
            });

            document.getElementById('task-filter').addEventListener('change', renderTasks);
            document.getElementById('task-sort').addEventListener('change', renderTasks);

            document.getElementById('reset-tasks').addEventListener('click', () => {
                if (confirm('Reset all tasks?')) {
                    tasks = []; saveTasks(); renderTasks();
                    if (window.calendar) window.calendar.refetchEvents();
                }
            });

            const dtBtn = document.getElementById('download-tasks');
            if (dtBtn) {
                dtBtn.addEventListener('click', () => {
                    if (tasks.length === 0) {
                        alert('No schedule data found.');
                        return;
                    }
                    let csv = "Subject|Description|Priority|Date|Time|Status\n";
                    tasks.forEach(t => {
                        csv += `${t.title || t.subject}|${t.desc}|${t.priority}|${t.date}|${t.time}|${t.completed ? 'Completed' : 'Pending'}\n`;
                    });
                    const blob = new Blob([csv], { type: 'text/csv' });
                    const url = window.URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = 'My_Schedule.csv';
                    a.click();
                    window.URL.revokeObjectURL(url);
                });
            }
        }

        window.toggleTask = (id) => {
            tasks = tasks.map(t => t.id === id ? { ...t, completed: !t.completed } : t);
            saveTasks();
            renderTasks();
            if (window.calendar) window.calendar.refetchEvents();
        };

        window.deleteTask = (id) => {
            tasks = tasks.filter(t => t.id !== id);
            saveTasks();
            renderTasks();
            if (window.calendar) window.calendar.refetchEvents();
        };

        function renderTasks() {
            const list = document.getElementById('task-list');
            if (!list) return;
            list.innerHTML = '';

            let filtered = [...tasks];
            const filterVal = document.getElementById('task-filter')?.value || 'all';
            if (filterVal === 'completed') filtered = filtered.filter(t => t.completed);
            else if (filterVal === 'pending') filtered = filtered.filter(t => !t.completed);

            const sortVal = document.getElementById('task-sort')?.value || 'due-date';

            const createTaskEl = (t) => {
                const li = document.createElement('li');
                li.className = `list-item ${t.completed ? 'completed' : ''}`;
                if (PRIORITY_COLORS[t.priority]) li.style.borderLeftColor = hexToCSS(PRIORITY_COLORS[t.priority]);

                let gCalLink = '#';
                try {
                    const [hours, minutes] = (t.time || "12:00").split(':');
                    let startDate = new Date(`${t.date}T${hours}:${minutes}:00`);
                    if (isNaN(startDate.getTime())) {
                        startDate = new Date();
                    }
                    const endDate = new Date(startDate.getTime() + (t.durationMinutes || 60) * 60000);

                    const formatGCalDate = (d) => {
                        return d.toISOString().replace(/-|:|\.\d\d\d/g, '');
                    };
                    const startStr = formatGCalDate(startDate);
                    const endStr = formatGCalDate(endDate);

                    const title = encodeURIComponent(t.title || t.subject);
                    const details = encodeURIComponent(t.desc);

                    gCalLink = `https://calendar.google.com/calendar/render?action=TEMPLATE&text=${title}&details=${details}&dates=${startStr}/${endStr}`;
                } catch (e) { console.error('GCal generate error', e) }

                li.innerHTML = `
                <div>
                    <strong>${sanitizeHTML(t.title || t.subject)}</strong>: ${sanitizeHTML(t.desc)} <br>
                    <small>Due: ${sanitizeHTML(t.date)} | Time: ${sanitizeHTML(t.time || '12:00')} (${t.durationMinutes || 60}m) | ${sanitizeHTML(t.priority)}</small>
                </div>
                <div class="task-actions d-flex gap-2 pe-2">
                    <a href="${gCalLink}" onclick="window.open(this.href, 'gcalPopup', 'width=600,height=600,scrollbars=yes,resizable=yes'); return false;" class="btn secondary-btn btn-sm hover-lift" title="Add to Google Calendar" aria-label="Add to Google Calendar" style="color: var(--primary); text-decoration: none;"><i class="fa-brands fa-google"></i></a>
                    <button class="btn secondary-btn btn-sm hover-lift" onclick="toggleTask(${t.id})" aria-label="Toggle Complete"><i class="fa-solid fa-check"></i></button>
                    <button class="btn danger-btn btn-sm hover-lift" onclick="deleteTask(${t.id})" aria-label="Delete Task"><i class="fa-solid fa-trash"></i></button>
                </div>
            `;
                return li;
            };

            if (sortVal === 'day-by-day') {
                const groups = {};
                filtered.forEach(t => {
                    const d = t.date || 'No Date';
                    if (!groups[d]) groups[d] = [];
                    groups[d].push(t);
                });
                const sortedDates = Object.keys(groups).sort((a, b) => new Date(a) - new Date(b));
                sortedDates.forEach(d => {
                    const dayHeader = document.createElement('h3');
                    dayHeader.className = 'mt-3 mb-2';
                    dayHeader.style.color = 'var(--primary)';
                    dayHeader.style.fontSize = '1.2em';
                    dayHeader.style.borderBottom = '1px solid var(--border-color)';
                    dayHeader.style.paddingBottom = '5px';

                    const groupTitle = new Date(d).toDateString() === new Date().toDateString() ? `Today (${d})` : d;
                    dayHeader.innerHTML = `<i class="fa-regular fa-calendar-days"></i> ${groupTitle}`;
                    list.appendChild(dayHeader);

                    groups[d].forEach(t => {
                        list.appendChild(createTaskEl(t));
                    });
                });
                return;
            }

            if (sortVal === 'due-date') {
                filtered.sort((a, b) => new Date(a.date) - new Date(b.date));
            } else if (sortVal === 'priority') {
                const pw = { 'High': 3, 'Medium': 2, 'Low': 1 };
                filtered.sort((a, b) => pw[b.priority] - pw[a.priority]);
            }

            filtered.forEach(t => {
                list.appendChild(createTaskEl(t));
            });
        }

        const saveTasks = () => localStorage.setItem('tasks', JSON.stringify(tasks));

        // Progress Tracker logic
        function updateProgress() {
            const pb = document.getElementById('progress-bar');
            if (!pb) return;

            const completed = tasks.filter(t => t.completed).length;
            const total = tasks.length;
            const remaining = total - completed;
            const percent = total === 0 ? 0 : Math.round((completed / total) * 100);

            document.getElementById('tasks-completed').textContent = completed;
            document.getElementById('tasks-remaining').textContent = remaining;
            document.getElementById('completion-percentage').textContent = percent;
            pb.style.width = percent + '%';
        }

        // Quote System
        const waitPromise = (ms) => new Promise(resolve => setTimeout(resolve, ms));
        const fetchQuote = async () => {
            const qc = document.getElementById('quote-content');
            if (!qc) return;

            try {
                await waitPromise(500);
                const res = await fetch('https://api.quotable.io/random');
                if (!res.ok) throw new Error('API down');
                const data = await res.json();
                qc.textContent = `"${data.content}"`;
                document.getElementById('quote-author').textContent = `- ${data.author}`;
            } catch (err) {
                qc.textContent = '"Failure is just an opportunity to begin again."';
                document.getElementById('quote-author').textContent = '- Keep Going';
            }
        };
        const nqb = document.getElementById('new-quote-btn');
        if (nqb) nqb.addEventListener('click', fetchQuote);

        // Timer Logic
        const td = document.getElementById('timer-display');
        if (td) {
            let timerInt;
            let timeRemaining = 25 * 60;
            let isTiming = false;
            const ONE_SECOND = 1e3;

            const updateTimerDisplay = () => {
                const m = Math.floor(timeRemaining / 60).toString().padStart(2, '0');
                const s = (timeRemaining % 60).toString().padStart(2, '0');
                td.textContent = `${m}:${s}`;
                if (isTiming) td.classList.add('pulse-anim');
                else td.classList.remove('pulse-anim');
            };

            document.getElementById('timer-start').addEventListener('click', () => {
                if (!isTiming) {
                    isTiming = true;
                    timerInt = setInterval(() => {
                        if (timeRemaining > 0) {
                            timeRemaining--;
                            updateTimerDisplay();
                        } else {
                            clearInterval(timerInt);
                            isTiming = false;
                            updateTimerDisplay();
                            playSound();
                            try {
                                if ("Notification" in window && Notification.permission !== "granted" && Notification.permission !== "denied") {
                                    Notification.requestPermission().catch(e => console.warn(e));
                                }
                            } catch (e) { }
                        }
                    }, ONE_SECOND);
                }
                updateTimerDisplay();
            });

            document.getElementById('timer-pause').addEventListener('click', () => {
                isTiming = false; clearInterval(timerInt); updateTimerDisplay();
            });

            document.getElementById('timer-reset').addEventListener('click', () => {
                isTiming = false; clearInterval(timerInt);
                const mode = document.querySelector('.mode-btn.active').id;
                timeRemaining = mode === 'mode-focus' ? 25 * 60 : 5 * 60;
                updateTimerDisplay();
            });

            document.getElementById('mode-focus').addEventListener('click', (e) => {
                document.getElementById('mode-break').classList.remove('active');
                e.target.classList.add('active');
                timeRemaining = 25 * 60; isTiming = false; clearInterval(timerInt); updateTimerDisplay();
            });

            document.getElementById('mode-break').addEventListener('click', (e) => {
                document.getElementById('mode-focus').classList.remove('active');
                e.target.classList.add('active');
                timeRemaining = 5 * 60; isTiming = false; clearInterval(timerInt); updateTimerDisplay();
            });
        }

        // Notes module
        const noteForm = document.getElementById('note-form');
        if (noteForm) {
            noteForm.addEventListener('submit', (e) => {
                e.preventDefault();
                notes.push({ id: Date.now(), title: document.getElementById('note-title').value, content: document.getElementById('note-content').value });
                saveNotes(); renderNotes(); noteForm.reset();
            });

            document.getElementById('note-search').addEventListener('input', (e) => renderNotes(e.target.value));
            document.getElementById('reset-notes').addEventListener('click', () => {
                if (confirm('Clear all notes?')) { notes = []; saveNotes(); renderNotes(); }
            });
        }

        window.deleteNote = (id) => {
            notes = notes.filter(n => n.id !== id); saveNotes(); renderNotes();
        }

        function renderNotes(filterStr = '') {
            const grid = document.getElementById('notes-grid');
            if (!grid) return;
            grid.innerHTML = '';
            const filtNotes = notes.filter(n => n.title.toLowerCase().includes(filterStr.toLowerCase()) || n.content.toLowerCase().includes(filterStr.toLowerCase()));
            filtNotes.forEach(n => {
                const d = document.createElement('div');
                d.className = 'note-card hover-lift';
                d.innerHTML = `
                <h4>${sanitizeHTML(n.title)}</h4>
                <p>${sanitizeHTML(n.content)}</p>
                <button class="btn danger-btn btn-sm mt-3 w-100" onclick="deleteNote(${n.id})"><i class="fa-solid fa-trash"></i> Drop</button>
            `;
                grid.appendChild(d);
            });
        }

        const saveNotes = () => localStorage.setItem('notes', JSON.stringify(notes));

        // Reminders Module
        const remForm = document.getElementById('reminder-form');
        if (remForm) {
            remForm.addEventListener('submit', (e) => {
                e.preventDefault();
                reminders.push({
                    id: Date.now(), title: document.getElementById('reminder-title').value,
                    date: document.getElementById('reminder-date').value, time: document.getElementById('reminder-time').value,
                    notified: false
                });
                saveReminders(); renderReminders(); remForm.reset();
            });
        }

        window.deleteReminder = (id) => {
            reminders = reminders.filter(x => x.id !== id); saveReminders(); renderReminders();
        }

        function renderReminders() {
            const list = document.getElementById('reminder-list');
            const rc = document.getElementById('reminder-counter');
            const bell = document.getElementById('notification-bell');

            if (rc) rc.textContent = reminders.length;
            if (bell) {
                if (reminders.length > 0) bell.classList.add('pulse-anim');
                else bell.classList.remove('pulse-anim');
            }

            if (!list) return;
            list.innerHTML = '';
            reminders.forEach(r => {
                const li = document.createElement('li');
                li.className = 'list-item';
                li.innerHTML = `
                <div>
                    <strong>${sanitizeHTML(r.title)}</strong><br>
                    <small>${sanitizeHTML(r.date)} ${sanitizeHTML(r.time)}</small>
                </div>
                <button class="btn danger-btn btn-sm hover-lift" onclick="deleteReminder(${r.id})">
                    <i class="fa-solid fa-trash"></i>
                </button>
            `;
                list.appendChild(li);
            });
        }

        const saveReminders = () => localStorage.setItem('reminders', JSON.stringify(reminders));

        // Background reminder execution
        setInterval(() => {
            const now = new Date();
            const currentDate = now.toISOString().split('T')[0];
            const currentTime = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`;

            reminders.forEach(r => {
                if (r.date === currentDate && r.time === currentTime && !r.notified) {
                    playSound();
                    try {
                        if ("Notification" in window && Notification.permission !== "granted" && Notification.permission !== "denied") {
                            Notification.requestPermission().catch(e => console.warn(e));
                        }
                    } catch (e) { }
                    r.notified = true; saveReminders();
                }
            });

            checkOverdueTasks();

        }, 60000);

        // Schedule module
        function renderSchedule() {
            ['morning', 'afternoon', 'evening', 'night'].forEach(period => {
                const b = document.querySelector(`#${period}-schedule .blocks`);
                if (b) {
                    b.innerHTML = '';
                    timeBlocks[period].forEach(tb => {
                        const el = document.createElement('div');
                        el.className = 'd-flex justify-content-between align-items-center mt-2 p-2 hover-lift';
                        el.style.background = 'rgba(0,0,0,0.05)'; el.style.borderRadius = '8px';
                        el.innerHTML = `
                        <span><strong>${sanitizeHTML(tb.time)}</strong> - ${sanitizeHTML(tb.desc)}</span>
                        <button class="btn danger-btn btn-sm" onclick="deleteTimeBlock('${period}', ${tb.id})"><i class="fa-solid fa-trash"></i></button>
                    `;
                        b.appendChild(el);
                    });
                }
            });
        }

        window.deleteTimeBlock = (period, id) => {
            timeBlocks[period] = timeBlocks[period].filter(x => x.id !== id); saveSchedule(); renderSchedule();
        }
        const saveSchedule = () => localStorage.setItem('timeBlocks', JSON.stringify(timeBlocks));

        // AI Planner Logic
        const aiGenerateBtn = document.getElementById('generate-ai-schedule');
        if (aiGenerateBtn) {
            const apiKeyInput = document.getElementById('ai-api-key');
            if (localStorage.getItem('geminiApiKey')) {
                apiKeyInput.value = localStorage.getItem('geminiApiKey');
            }

            apiKeyInput.addEventListener('change', (e) => {
                localStorage.setItem('geminiApiKey', e.target.value);
            });

            let uploadedFileObj = null;

            const fileInput = document.getElementById('ai-syllabus-file');
            if (fileInput) {
                fileInput.addEventListener('change', (e) => {
                    const file = e.target.files[0];
                    if (!file) return;

                    if (file.type.startsWith('text/') || file.name.endsWith('.md') || file.name.endsWith('.json')) {
                        const reader = new FileReader();
                        reader.onload = (event) => {
                            document.getElementById('ai-syllabus').value = event.target.result;
                            uploadedFileObj = null;
                        };
                        reader.readAsText(file);
                    } else {
                        uploadedFileObj = file;
                        document.getElementById('ai-syllabus').value = `[File Ready for AI: ${file.name}] - Size: ${(file.size / 1024).toFixed(1)} KB`;
                    }
                });
            }

            aiGenerateBtn.addEventListener('click', async () => {
                const apiKey = apiKeyInput.value.trim();
                const syllabus = document.getElementById('ai-syllabus').value.trim();
                if (!apiKey) {
                    alert('Please enter your Gemini API Key. You can get a free one from Google AI Studio.');
                    return;
                }
                if (!syllabus && !uploadedFileObj) {
                    alert('Please enter your syllabus or attach a file.');
                    return;
                }

                document.getElementById('ai-loading').classList.remove('hidden');
                document.getElementById('ai-loading').innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i> Processing and Generating Schedule...';
                aiGenerateBtn.disabled = true;

                try {
                    let parts = [{ text: `I am a student using a study planner. Here is my syllabus/topics context:\n${syllabus}\nCan you break this entire syllabus down into a comprehensive day-by-day distribution spanning multiple days? Return the output STRICTLY as a JSON array of objects, where each object has:\n- subject: a short string title of the module/topic\n- desc: description or details of what to study\n- priority: either "High", "Medium", or "Low"\n- daysFromNow: an integer representing how many days from today this task should be scheduled (e.g., 0 for today, 1 for tomorrow, 2 for the day after, distributing tasks logically across days)\n- time: a string time in 24h format for when to study (e.g. "09:00", "14:30")\n- durationMinutes: an integer representing how long this study session will take in minutes (e.g., 60, 90)\nEnsure you schedule all parts of the syllabus! Only return the JSON array, no markdown or comments. Do not include markdown code block syntax like \`\`\`json.` }];

                    if (uploadedFileObj) {
                        try {
                            const mimeType = uploadedFileObj.type || 'application/pdf';

                            const base64Data = await new Promise((resolve, reject) => {
                                const reader = new FileReader();
                                reader.onload = () => {
                                    const result = reader.result;
                                    const b64 = result.includes(',') ? result.split(',')[1] : result;
                                    resolve(b64);
                                };
                                reader.onerror = (e) => reject(new Error("Failed to read file"));
                                reader.readAsDataURL(uploadedFileObj);
                            });

                            parts.push({
                                inlineData: {
                                    mimeType: mimeType,
                                    data: base64Data
                                }
                            });
                        } catch (uploadErr) {
                            throw new Error(`File Upload Error: ${uploadErr.message}`);
                        }
                    }

                    const response = await fetch(`https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=${apiKey}`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ contents: [{ parts: parts }] })
                    });

                    if (!response.ok) {
                        const errData = await response.json();
                        console.error('API Error Response:', errData);
                        throw new Error(`API Error: ${errData.error?.message || 'Unknown error'}`);
                    }

                    const data = await response.json();

                    if (!data.candidates || !data.candidates[0] || !data.candidates[0].content) {
                        throw new Error(`Invalid API Response: ${data.promptFeedback?.blockReason || 'Blocked by safety settings or empty content.'}`);
                    }

                    let aiText = data.candidates[0].content.parts[0].text;

                    aiText = aiText.replace(/```json/gi, '').replace(/```/g, '').trim();
                    const startIndex = aiText.indexOf('[');
                    const endIndex = aiText.lastIndexOf(']');
                    if (startIndex !== -1 && endIndex !== -1) {
                        aiText = aiText.substring(startIndex, endIndex + 1);
                    }

                    let generatedItems;
                    try {
                        generatedItems = JSON.parse(aiText);
                    } catch (e) {
                        throw new Error("AI returned malformed JSON: " + e.message);
                    }

                    if (!Array.isArray(generatedItems)) {
                        generatedItems = generatedItems.tasks || generatedItems.items || generatedItems.schedule || [generatedItems];
                    }

                    const currentDate = new Date();

                    generatedItems.forEach((item, index) => {
                        const taskDate = new Date(currentDate);
                        taskDate.setDate(taskDate.getDate() + Number(item.daysFromNow || 0));
                        const dateStr = taskDate.toISOString().split('T')[0];

                        const newId = Date.now() + Math.floor(Math.random() * 100000) + index;
                        const newTask = new Task(newId, item.subject || "Study Task", item.desc || "", item.priority || "Medium", dateStr, item.time || "12:00", item.durationMinutes || 60);
                        tasks.push(newTask);

                        reminders.push({
                            id: newId + 500000,
                            title: `Study Reminder: ${item.subject || "Study Task"}`,
                            date: dateStr,
                            time: item.time || "09:00",
                            notified: false
                        });
                    });

                    saveTasks(); saveReminders();
                    if (document.getElementById('task-list')) renderTasks();
                    if (document.getElementById('reminder-list')) renderReminders();
                    if (window.calendar) window.calendar.refetchEvents();

                    alert('Successfully generated and added tasks and reminders!');
                    document.getElementById('ai-syllabus').value = '';

                } catch (err) {
                    console.error(err);
                    alert("Error generating schedule. Make sure this is a valid JSON generation and your API key works. " + err.message);
                } finally {
                    document.getElementById('ai-loading').classList.add('hidden');
                    aiGenerateBtn.disabled = false;
                }
            });
        }

        let activeSchedulePeriod = null;
        const sModal = document.getElementById('schedule-modal');
        const sTimeInp = document.getElementById('schedule-time-input');
        const sDescInp = document.getElementById('schedule-desc-input');
        const sSaveBtn = document.getElementById('save-schedule-btn');

        document.querySelectorAll('.add-time-block').forEach(btn => {
            btn.addEventListener('click', (e) => {
                activeSchedulePeriod = e.currentTarget.getAttribute('data-period');
                if (sModal) {
                    sTimeInp.value = '';
                    sDescInp.value = '';
                    sModal.classList.remove('hidden');
                }
            });
        });

        if (sSaveBtn) {
            sSaveBtn.addEventListener('click', () => {
                if (!sTimeInp.value || !sDescInp.value) return;
                if (activeSchedulePeriod && timeBlocks[activeSchedulePeriod]) {
                    timeBlocks[activeSchedulePeriod].push({ id: Date.now(), time: sTimeInp.value, desc: sDescInp.value });
                    saveSchedule();
                    renderSchedule();
                    sModal.classList.add('hidden');
                }
            });
        }

        const sCloseBtn = document.querySelector('.close-schedule-modal');
        if (sCloseBtn) {
            sCloseBtn.addEventListener('click', () => sModal.classList.add('hidden'));
        }

        // Global Search
        const gSearch = document.getElementById('global-search');
        const searchModal = document.getElementById('search-results-modal');

        if (gSearch && searchModal) {
            gSearch.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    const q = gSearch.value.toLowerCase();
                    if (!q) return;

                    const cont = document.getElementById('search-results-container');
                    cont.innerHTML = '';

                    const tRes = tasks.filter(t => (t.title || t.subject).toLowerCase().includes(q) || t.desc.toLowerCase().includes(q));
                    const nRes = notes.filter(n => n.title.toLowerCase().includes(q) || n.content.toLowerCase().includes(q));
                    const rRes = reminders.filter(r => r.title.toLowerCase().includes(q));

                    if (tRes.length === 0 && nRes.length === 0 && rRes.length === 0) {
                        cont.innerHTML = '<p>No results found.</p>';
                    } else {
                        if (tRes.length > 0) cont.innerHTML += `<h4>Tasks</h4><ul>${tRes.map(x => `<li>${sanitizeHTML(x.title || x.subject)}</li>`).join('')}</ul>`;
                        if (nRes.length > 0) cont.innerHTML += `<h4>Notes</h4><ul>${nRes.map(x => `<li>${sanitizeHTML(x.title)}</li>`).join('')}</ul>`;
                        if (rRes.length > 0) cont.innerHTML += `<h4>Reminders</h4><ul>${rRes.map(x => `<li>${sanitizeHTML(x.title)}</li>`).join('')}</ul>`;
                    }
                    searchModal.classList.remove('hidden');
                }
            });

            document.querySelector('.close-modal').addEventListener('click', () => {
                searchModal.classList.add('hidden');
            });
        }

        const resetRemBtn = document.getElementById('reset-reminders-main');
        if (resetRemBtn) {
            resetRemBtn.addEventListener('click', () => {
                if (confirm('Clear all reminders?')) { reminders = []; saveReminders(); renderReminders(); }
            });
        }

        const resetAllBtn = document.getElementById('reset-all');
        if (resetAllBtn) {
            resetAllBtn.addEventListener('click', () => {
                if (confirm('Reset EVERYTHING? This action is irreversible.')) {
                    localStorage.clear();
                    window.location.href = 'index.html';
                }
            });
        }

        // Global Load
        loadData();

    } catch (criticalErr) {
        document.body.innerHTML += `<div style="position:fixed;bottom:10px;right:10px;background:red;color:white;z-index:99999;padding:20px;border-radius:10px;box-shadow:0 10px 30px rgba(0,0,0,0.5);">CRITICAL JS ERROR: ${criticalErr.message}</div>`;
        console.error(criticalErr);
    }
});
