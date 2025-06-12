// Konfiguracja API
const API_CONFIG = {
    baseUrl: window.location.origin,
    endpoints: {
        questions: '/api/questions'
    }
};

// Główna klasa aplikacji
class QuestionApp {
    constructor() {
        this.questions = [];
        this.apiUrl = `${API_CONFIG.baseUrl}${API_CONFIG.endpoints.questions}`;
        this.questionToDelete = null;

        // Inicjalizacja elementów DOM
        this.initializeElements();

        // Bindowanie metod
        this.bindMethods();

        // Inicjalizacja event listenerów
        this.initEventListeners();

        // Ładowanie pytań
        this.loadQuestions();
    }

    initializeElements() {
        this.questionsContainer = document.getElementById('questions-container');
        this.addQuestionBtn = document.getElementById('add-question-btn');
        this.addQuestionModal = document.getElementById('add-question-modal');
        this.editQuestionModal = document.getElementById('edit-question-modal');
        this.deleteConfirmationModal = document.getElementById('delete-confirmation-modal');
        this.questionForm = document.getElementById('question-form');
        this.editQuestionForm = document.getElementById('edit-question-form');
        this.loader = document.getElementById('loader');
    }

    bindMethods() {
        this.loadQuestions = this.loadQuestions.bind(this);
        this.renderQuestions = this.renderQuestions.bind(this);
        this.addQuestion = this.addQuestion.bind(this);
        this.editQuestion = this.editQuestion.bind(this);
        this.deleteQuestion = this.deleteQuestion.bind(this);
        this.handleVote = this.handleVote.bind(this);
    }

    initEventListeners() {
        // Otwarcie modala dodawania pytania
        this.addQuestionBtn.addEventListener('click', () => {
            this.openAddModal();
        });

        // Zamknięcie modali
        document.querySelectorAll('.close-modal').forEach(closeBtn => {
            closeBtn.addEventListener('click', () => {
                this.closeAllModals();
            });
        });

        // Zamknięcie modali przez kliknięcie w tło
        [this.addQuestionModal, this.editQuestionModal, this.deleteConfirmationModal].forEach(modal => {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    this.closeAllModals();
                }
            });
        });

        // Obsługa formularza dodawania pytania
        this.questionForm.addEventListener('submit', (e) => {
            e.preventDefault();
            this.addQuestion();
        });

        // Obsługa formularza edycji pytania
        this.editQuestionForm.addEventListener('submit', (e) => {
            e.preventDefault();
            this.editQuestion();
        });

        // Obsługa potwierdzenia usunięcia
        document.getElementById('confirm-delete').addEventListener('click', () => {
            if (this.questionToDelete) {
                this.deleteQuestion(this.questionToDelete);
            }
        });

        document.getElementById('cancel-delete').addEventListener('click', () => {
            this.closeAllModals();
            this.questionToDelete = null;
        });

        // Obsługa klawisza Escape
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                this.closeAllModals();
            }
        });
    }

    // Wyświetlanie/ukrywanie loadera
    showLoader() {
        this.loader.classList.remove('hidden');
    }

    hideLoader() {
        this.loader.classList.add('hidden');
    }

    // Ładowanie pytań z API
    async loadQuestions() {
        this.showLoader();
        try {
            const response = await fetch(this.apiUrl);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            this.questions = await response.json();
            this.renderQuestions();
        } catch (error) {
            console.error('Błąd podczas pobierania pytań:', error);
            this.showError('Nie udało się załadować pytań. Sprawdź połączenie z serwerem.');
        } finally {
            this.hideLoader();
        }
    }

    // Wyświetlanie pytań na stronie
    renderQuestions() {
        if (!this.questionsContainer) return;

        this.questionsContainer.innerHTML = '';

        if (this.questions.length === 0) {
            this.questionsContainer.innerHTML = `
                <div class="no-questions">
                    <h2>Brak pytań do wyświetlenia</h2>
                    <p>Dodaj pierwsze pytanie, aby rozpocząć głosowanie!</p>
                </div>
            `;
            return;
        }

        // Sortowanie pytań według daty utworzenia (najnowsze pierwsze)
        const sortedQuestions = [...this.questions].sort((a, b) =>
            new Date(b.createtime) - new Date(a.createtime)
        );

        sortedQuestions.forEach(question => {
            const questionCard = this.createQuestionCard(question);
            this.questionsContainer.appendChild(questionCard);
        });
    }

    createQuestionCard(question) {
        const questionCard = document.createElement('div');
        questionCard.className = 'question-card';

        const createTime = new Date(question.createtime).toLocaleDateString('pl-PL', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

        const endTime = question.endtime
            ? new Date(question.endtime).toLocaleDateString('pl-PL', {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            })
            : 'Brak terminu';

        const isExpired = question.endtime && new Date(question.endtime) < new Date();
        const totalVotes = question.yesvote + question.novote;
        const yesPercentage = totalVotes > 0 ? Math.round((question.yesvote / totalVotes) * 100) : 0;
        const noPercentage = totalVotes > 0 ? Math.round((question.novote / totalVotes) * 100) : 0;

        questionCard.innerHTML = `
            <div class="question-header">
                <h3 class="question-title">${this.escapeHtml(question.name)}</h3>
                <div class="question-date">
                    <div><strong>Utworzono:</strong> ${createTime}</div>
                    <div><strong>Termin:</strong> ${endTime}</div>
                    ${isExpired ? '<div style="color: #e74c3c; font-weight: bold;">ZAKOŃCZONE</div>' : ''}
                </div>
            </div>
            <div class="question-description">${this.escapeHtml(question.description)}</div>
            <div class="question-votes">
                <button class="vote-btn vote-yes" data-id="${question.id}" data-vote="yes" ${isExpired ? 'disabled' : ''}>
                    <span>👍 Tak</span>
                    <span class="vote-count">${question.yesvote}</span>
                    <small>${yesPercentage}%</small>
                </button>
                <button class="vote-btn vote-no" data-id="${question.id}" data-vote="no" ${isExpired ? 'disabled' : ''}>
                    <span>👎 Nie</span>
                    <span class="vote-count">${question.novote}</span>
                    <small>${noPercentage}%</small>
                </button>
            </div>
            <div class="action-buttons">
                <button class="btn btn-secondary edit-btn" data-id="${question.id}">
                    ✏️ Edytuj
                </button>
                <button class="btn btn-danger delete-btn" data-id="${question.id}">
                    🗑️ Usuń
                </button>
            </div>
        `;

        // Dodanie obsługi głosowania
        const voteButtons = questionCard.querySelectorAll('.vote-btn');
        voteButtons.forEach(button => {
            if (!button.disabled) {
                button.addEventListener('click', (e) => {
                    const id = e.currentTarget.getAttribute('data-id');
                    const voteType = e.currentTarget.getAttribute('data-vote');
                    this.handleVote(id, voteType);
                });
            }
        });

        // Dodanie obsługi edycji
        const editBtn = questionCard.querySelector('.edit-btn');
        editBtn.addEventListener('click', () => {
            this.openEditModal(question);
        });

        // Dodanie obsługi usuwania
        const deleteBtn = questionCard.querySelector('.delete-btn');
        deleteBtn.addEventListener('click', () => {
            this.openDeleteModal(question.id);
        });

        return questionCard;
    }

    // Escape HTML dla bezpieczeństwa
    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    // Otwieranie modala dodawania
    openAddModal() {
        this.clearForm(this.questionForm);
        this.clearError('add-question-error');
        this.addQuestionModal.style.display = 'flex';
        document.getElementById('question-name').focus();
    }

    // Otwieranie modala edycji
    openEditModal(question) {
        document.getElementById('edit-question-id').value = question.id;
        document.getElementById('edit-question-name').value = question.name;
        document.getElementById('edit-question-description').value = question.description;

        if (question.endtime) {
            const endTime = new Date(question.endtime);
            const formattedDate = endTime.toISOString().slice(0, 16);
            document.getElementById('edit-question-end-time').value = formattedDate;
        } else {
            document.getElementById('edit-question-end-time').value = '';
        }

        this.clearError('edit-question-error');
        this.editQuestionModal.style.display = 'flex';
        document.getElementById('edit-question-name').focus();
    }

    // Otwieranie modala usuwania
    openDeleteModal(questionId) {
        this.questionToDelete = questionId;
        this.deleteConfirmationModal.style.display = 'flex';
    }

    // Zamykanie wszystkich modali
    closeAllModals() {
        this.addQuestionModal.style.display = 'none';
        this.editQuestionModal.style.display = 'none';
        this.deleteConfirmationModal.style.display = 'none';
        this.questionToDelete = null;
    }

    // Czyszczenie formularza
    clearForm(form) {
        form.reset();
    }

    // Dodawanie nowego pytania
    async addQuestion() {
        const nameInput = document.getElementById('question-name');
        const descriptionInput = document.getElementById('question-description');
        const endTimeInput = document.getElementById('question-end-time');

        const name = nameInput.value.trim();
        const description = descriptionInput.value.trim();
        const endtime = endTimeInput.value ? new Date(endTimeInput.value).toISOString() : null;

        // Walidacja
        if (!this.validateQuestionData(name, description, endtime, 'add-question-error')) {
            return;
        }

        const newQuestion = {
            name,
            description,
            endtime,
            yesvote: 0,
            novote: 0
        };

        try {
            const response = await fetch(this.apiUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newQuestion)
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const addedQuestion = await response.json();
            this.questions.push(addedQuestion);
            this.renderQuestions();

            this.closeAllModals();
            this.showSuccess('Pytanie zostało dodane pomyślnie!');
        } catch (error) {
            console.error('Błąd podczas dodawania pytania:', error);
            this.showError('Nie udało się dodać pytania. Spróbuj ponownie później.', 'add-question-error');
        }
    }

    // Edycja pytania
    async editQuestion() {
        const idInput = document.getElementById('edit-question-id');
        const nameInput = document.getElementById('edit-question-name');
        const descriptionInput = document.getElementById('edit-question-description');
        const endTimeInput = document.getElementById('edit-question-end-time');

        const id = idInput.value;
        const name = nameInput.value.trim();
        const description = descriptionInput.value.trim();
        const endtime = endTimeInput.value ? new Date(endTimeInput.value).toISOString() : null;

        // Walidacja
        if (!this.validateQuestionData(name, description, endtime, 'edit-question-error')) {
            return;
        }

        // Znajdź aktualne pytanie
        const currentQuestion = this.questions.find(q => q.id == id);
        if (!currentQuestion) {
            this.showError('Nie znaleziono pytania do edycji', 'edit-question-error');
            return;
        }

        const updatedQuestion = {
            id: parseInt(id),
            name,
            description,
            endtime,
            yesvote: currentQuestion.yesvote,
            novote: currentQuestion.novote
        };

        try {
            const response = await fetch(`${this.apiUrl}/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(updatedQuestion)
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const editedQuestion = await response.json();

            // Aktualizacja listy pytań
            const index = this.questions.findIndex(q => q.id == id);
            if (index !== -1) {
                this.questions[index] = editedQuestion;
            }

            this.renderQuestions();
            this.closeAllModals();
            this.showSuccess('Pytanie zostało zaktualizowane pomyślnie!');
        } catch (error) {
            console.error('Błąd podczas aktualizacji pytania:', error);
            this.showError('Nie udało się zaktualizować pytania. Spróbuj ponownie później.', 'edit-question-error');
        }
    }

    // Usuwanie pytania
    async deleteQuestion(id) {
        try {
            const response = await fetch(`${this.apiUrl}/${id}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            // Usunięcie z lokalnej listy
            this.questions = this.questions.filter(q => q.id != id);
            this.renderQuestions();
            this.closeAllModals();
            this.showSuccess('Pytanie zostało usunięte pomyślnie!');
        } catch (error) {
            console.error('Błąd podczas usuwania pytania:', error);
            this.showError('Nie udało się usunąć pytania. Spróbuj ponownie później.');
        }
    }

    // Obsługa głosowania
    async handleVote(id, voteType) {
        try {
            const response = await fetch(`${this.apiUrl}/${id}/vote/${voteType}`, {
                method: 'POST'
            });

            if (!response.ok) {
                if (response.status === 400) {
                    this.showError('Już oddałeś głos na to pytanie!');
                    return;
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const updatedQuestion = await response.json();

            // Aktualizacja listy pytań
            const index = this.questions.findIndex(q => q.id == id);
            if (index !== -1) {
                this.questions[index] = updatedQuestion;
            }

            this.renderQuestions();
            this.showSuccess('Twój głos został zarejestrowany!');
        } catch (error) {
            console.error('Błąd podczas głosowania:', error);
            this.showError('Nie udało się oddać głosu. Spróbuj ponownie później.');
        }
    }

    // Walidacja danych pytania
    validateQuestionData(name, description, endtime, errorElementId) {
        if (!name || name.length < 3) {
            this.showError('Nazwa pytania musi mieć co najmniej 3 znaki.', errorElementId);
            return false;
        }

        if (!description || description.length < 10) {
            this.showError('Opis pytania musi mieć co najmniej 10 znaków.', errorElementId);
            return false;
        }

        if (endtime && new Date(endtime) <= new Date()) {
            this.showError('Termin zakończenia musi być w przyszłości.', errorElementId);
            return false;
        }

        return true;
    }

    // Wyświetlanie komunikatów o błędach
    showError(message, elementId = 'main-error') {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.classList.remove('hidden');

            setTimeout(() => {
                errorElement.classList.add('hidden');
            }, 5000);
        } else {
            alert(message);
        }
    }

    // Czyszczenie komunikatów o błędach
    clearError(elementId) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.classList.add('hidden');
        }
    }

    // Wyświetlanie komunikatów o sukcesie
    showSuccess(message, elementId = 'main-success') {
        const successElement = document.getElementById(elementId);
        if (successElement) {
            successElement.textContent = message;
            successElement.classList.remove('hidden');

            setTimeout(() => {
                successElement.classList.add('hidden');
            }, 3000);
        }
    }
}

// Inicjalizacja aplikacji po załadowaniu strony
document.addEventListener('DOMContentLoaded', () => {
    new QuestionApp();
});