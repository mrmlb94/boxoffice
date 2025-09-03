const BASE_URL = "http://localhost:8080/api";

const listEl = document.getElementById('todo-list');
const formEl = document.getElementById('todo-form');
const refreshBtn = document.getElementById('refresh');

let todos = [];

function parseTags(input) {
  return input
      .split(',')
      .map(t => t.trim())
      .filter(Boolean);
}

async function fetchTodos() {
  try {
    const res = await fetch(`${BASE_URL}/todos`);
    todos = await res.json();
    renderTodos();
  } catch (e) {
    console.error('Error loading todos', e);
    alert('Error fetching task list. Make sure the backend is running.');
  }
}

function renderTodos() {
  listEl.innerHTML = '';
  todos.forEach(t => {
    const li = document.createElement('li');
    li.className = 'todo-item' + (t.done ? ' done' : '');

    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.className = 'checkbox';
    checkbox.checked = !!t.done;
    checkbox.addEventListener('change', () => toggleDone(t));

    const content = document.createElement('div');
    const title = document.createElement('div');
    title.className = 'todo-title';
    title.textContent = t.title || '(Untitled)';

    const desc = document.createElement('div');
    desc.className = 'todo-desc';
    desc.textContent = t.description || '';

    const tagsWrap = document.createElement('div');
    tagsWrap.className = 'tags';
    (t.tags || []).forEach(tag => {
      const chip = document.createElement('span');
      chip.className = 'tag';
      chip.textContent = tag;
      tagsWrap.appendChild(chip);
    });

    content.appendChild(title);
    content.appendChild(desc);
    content.appendChild(tagsWrap);

    const actions = document.createElement('div');
    actions.className = 'item-actions';

    const editBtn = document.createElement('button');
    editBtn.className = 'btn';
    editBtn.textContent = 'Edit';
    editBtn.addEventListener('click', () => editTodo(t));

    const delBtn = document.createElement('button');
    delBtn.className = 'btn';
    delBtn.textContent = 'Delete';
    delBtn.addEventListener('click', () => deleteTodo(t.id));

    actions.appendChild(editBtn);
    actions.appendChild(delBtn);

    li.appendChild(checkbox);
    li.appendChild(content);
    li.appendChild(actions);
    listEl.appendChild(li);
  });
}

async function addTodo(e) {
  e.preventDefault();
  const title = document.getElementById('title').value.trim();
  const description = document.getElementById('description').value.trim();
  const tags = parseTags(document.getElementById('tags').value);
  if (!title) { alert('Title is required'); return; }
  try {
    const res = await fetch(`${BASE_URL}/todos`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title, description, tags, done: false })
    });
    if (!res.ok) throw new Error('Create failed');
    const created = await res.json();
    todos.unshift(created);
    renderTodos();
    formEl.reset();
  } catch (e) {
    console.error(e);
    alert('Failed to create item');
  }
}

async function editTodo(t) {
  const newTitle = prompt('Title:', t.title || '');
  if (newTitle === null) return; // cancel
  const newDesc = prompt('Description:', t.description || '');
  if (newDesc === null) return;
  const newTagsStr = prompt('Tags (separate with commas):', (t.tags || []).join(', '));
  if (newTagsStr === null) return;
  const newTags = parseTags(newTagsStr);

  try {
    const res = await fetch(`${BASE_URL}/todos/${t.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: newTitle, description: newDesc, tags: newTags, done: t.done })
    });
    if (!res.ok) throw new Error('Update failed');
    const updated = await res.json();
    todos = todos.map(x => x.id === t.id ? updated : x);
    renderTodos();
  } catch (e) {
    console.error(e);
    alert('Failed to update item');
  }
}

async function toggleDone(t) {
  try {
    const res = await fetch(`${BASE_URL}/todos/${t.id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ title: t.title, description: t.description, tags: t.tags || [], done: !t.done })
    });
    if (!res.ok) throw new Error('Toggle failed');
    const updated = await res.json();
    todos = todos.map(x => x.id === t.id ? updated : x);
    renderTodos();
  } catch (e) {
    console.error(e);
    alert('Error changing status');
  }
}

async function deleteTodo(id) {
  if (!confirm('Delete this item?')) return;
  try {
    const res = await fetch(`${BASE_URL}/todos/${id}`, { method: 'DELETE' });
    if (res.status !== 204) throw new Error('Delete failed');
    todos = todos.filter(t => t.id !== id);
    renderTodos();
  } catch (e) {
    console.error(e);
    alert('Failed to delete item');
  }
}

formEl.addEventListener('submit', addTodo);
refreshBtn.addEventListener('click', fetchTodos);

fetchTodos();
