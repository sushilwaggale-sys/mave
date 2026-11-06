const apiBaseUrl = window.APP_CONFIG?.API_BASE_URL;
const employeeRows = document.getElementById('employeeRows');
const searchInput = document.getElementById('searchInput');
const departmentFilter = document.getElementById('departmentFilter');
const modalElement = document.getElementById('employeeModal');
const employeeModal = new bootstrap.Modal(modalElement);
const employeeForm = document.getElementById('employeeForm');
let employees = [];
let editingId = null;

if (!apiBaseUrl) {
  showAlert('The API URL is not configured. Restart the frontend with API_BASE_URL set.', 'danger');
}

async function apiRequest(path, options = {}) {
  const response = await fetch(`${apiBaseUrl}${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...options.headers }
  });
  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try {
      const body = await response.json();
      message = body.detail || body.message || body.error || message;
    } catch {
      // Keep the HTTP status message when the server does not return JSON.
    }
    throw new Error(message);
  }
  return response.status === 204 ? null : response.json();
}

async function loadEmployees() {
  try {
    employees = await apiRequest('/api/employees');
    renderDashboard();
    renderEmployees();
  } catch (error) {
    employeeRows.innerHTML = `<tr><td colspan="6" class="empty-state">Unable to load employees. ${escapeHtml(error.message)}</td></tr>`;
    showAlert(`Could not connect to the employee API: ${error.message}`, 'danger');
  }
}

function renderDashboard() {
  const activeCount = employees.filter(employee => employee.active).length;
  const departmentCounts = employees.reduce((counts, employee) => {
    counts[employee.department] = (counts[employee.department] || 0) + 1;
    return counts;
  }, {});
  const departments = Object.entries(departmentCounts).sort(([first], [second]) => first.localeCompare(second));
  document.getElementById('totalEmployees').textContent = employees.length;
  document.getElementById('activeEmployees').textContent = activeCount;
  document.getElementById('departmentTotal').textContent = departments.length;
  document.getElementById('departmentChart').innerHTML = departments.length
    ? departments.map(([department, count]) => `
      <div class="department-item">
        <div class="department-meta"><span>${escapeHtml(department)}</span><strong>${count}</strong></div>
        <div class="bar-track"><div class="bar-fill" style="width:${Math.max(8, count / employees.length * 100)}%"></div></div>
      </div>`).join('')
    : '<div class="empty-state">No departments yet.</div>';
  const selectedDepartment = departmentFilter.value;
  const departmentOptions = departments.map(([department]) => `<option value="${escapeHtml(department)}">${escapeHtml(department)}</option>`).join('');
  departmentFilter.innerHTML = `<option value="">All departments</option>${departmentOptions}`;
  if (departments.some(([department]) => department === selectedDepartment)) {
    departmentFilter.value = selectedDepartment;
  }
  document.getElementById('departmentOptions').innerHTML = departments
    .map(([department]) => `<option value="${escapeHtml(department)}"></option>`).join('');
}

function renderEmployees() {
  const query = searchInput.value.trim().toLocaleLowerCase();
  const department = departmentFilter.value;
  const filtered = employees.filter(employee =>
    employee.name.toLocaleLowerCase().includes(query) &&
    (!department || employee.department === department)
  );
  document.getElementById('employeeCount').textContent = filtered.length;
  document.getElementById('tableSummary').textContent =
    `Showing ${filtered.length} of ${employees.length} employee${employees.length === 1 ? '' : 's'}`;
  if (!filtered.length) {
    employeeRows.innerHTML = '<tr><td colspan="6" class="empty-state">No employees match your search.</td></tr>';
    return;
  }
  employeeRows.innerHTML = filtered.map((employee, index) => {
    const initials = employee.name.split(/\s+/).slice(0, 2).map(part => part[0]).join('').toUpperCase();
    return `<tr>
      <td><div class="employee-person"><div class="person-avatar avatar-${index % 5}">${escapeHtml(initials)}</div><div><div class="person-name">${escapeHtml(employee.name)}</div><div class="person-email">${escapeHtml(employee.email)}</div></div></div></td>
      <td>${escapeHtml(employee.department)}</td>
      <td>${escapeHtml(employee.designation)}</td>
      <td><span class="status-badge ${employee.active ? '' : 'inactive'}">${employee.active ? 'Active' : 'Inactive'}</span></td>
      <td class="text-end">${formatSalary(employee.salary)}</td>
      <td><div class="action-buttons"><button class="action-button" type="button" aria-label="Edit ${escapeHtml(employee.name)}" data-action="edit" data-id="${escapeHtml(employee.employeeId)}" title="Edit">✎</button><button class="action-button delete" type="button" aria-label="Delete ${escapeHtml(employee.name)}" data-action="delete" data-id="${escapeHtml(employee.employeeId)}" title="Delete">×</button></div></td>
    </tr>`;
  }).join('');
}

function formatSalary(salary) {
  return new Intl.NumberFormat(undefined, { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(salary);
}

function escapeHtml(value) {
  return String(value ?? '').replace(/[&<>"']/g, character => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
  })[character]);
}

function showAlert(message, type = 'success') {
  document.getElementById('alertArea').innerHTML = `<div class="alert alert-${type} alert-dismissible fade show" role="alert">${escapeHtml(message)}<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button></div>`;
}

function resetForm() {
  employeeForm.reset();
  employeeForm.elements.active.checked = true;
  employeeForm.elements.employeeId.disabled = false;
  editingId = null;
  document.getElementById('modalTitle').textContent = 'Add employee';
  document.getElementById('saveEmployeeButton').textContent = 'Save employee';
  document.getElementById('formError').textContent = '';
}

document.getElementById('addEmployeeButton').addEventListener('click', resetForm);
modalElement.addEventListener('hidden.bs.modal', resetForm);
searchInput.addEventListener('input', renderEmployees);
departmentFilter.addEventListener('change', renderEmployees);

employeeRows.addEventListener('click', async event => {
  const button = event.target.closest('button[data-action]');
  if (!button) return;
  const employee = employees.find(item => item.employeeId === button.dataset.id);
  if (!employee) return;
  if (button.dataset.action === 'edit') {
    editingId = employee.employeeId;
    for (const [key, value] of Object.entries(employee)) {
      if (employeeForm.elements[key]) employeeForm.elements[key].value = value;
    }
    employeeForm.elements.active.checked = employee.active;
    employeeForm.elements.employeeId.disabled = true;
    document.getElementById('modalTitle').textContent = 'Edit employee';
    document.getElementById('saveEmployeeButton').textContent = 'Save changes';
    employeeModal.show();
    return;
  }
  if (window.confirm(`Delete ${employee.name} (${employee.employeeId})? This cannot be undone.`)) {
    try {
      await apiRequest(`/api/employees/${encodeURIComponent(employee.employeeId)}`, { method: 'DELETE' });
      showAlert(`${employee.name} was deleted.`);
      await loadEmployees();
    } catch (error) {
      showAlert(`Could not delete employee: ${error.message}`, 'danger');
    }
  }
});

employeeForm.addEventListener('submit', async event => {
  event.preventDefault();
  const formData = new FormData(employeeForm);
  const employee = Object.fromEntries(formData.entries());
  employee.employeeId = editingId || employee.employeeId;
  employee.salary = Number(employee.salary);
  employee.active = employeeForm.elements.active.checked;
  const button = document.getElementById('saveEmployeeButton');
  button.disabled = true;
  document.getElementById('formError').textContent = '';
  try {
    await apiRequest(editingId
      ? `/api/employees/${encodeURIComponent(editingId)}`
      : '/api/employees', {
      method: editingId ? 'PUT' : 'POST',
      body: JSON.stringify(employee)
    });
    employeeModal.hide();
    showAlert(editingId ? 'Employee details were updated.' : 'Employee was added.');
    await loadEmployees();
  } catch (error) {
    document.getElementById('formError').textContent = error.message;
  } finally {
    button.disabled = false;
  }
});

loadEmployees();
