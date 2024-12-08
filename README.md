# Todo Application

Full-stack Todo application built with Spring Boot backend and React frontend. This application allows users to manage their tasks efficiently with features like user authentication, task creation, updating, and deletion.

## Project Structure

```
todo-app/
├── backend/    # Spring Boot application
└── frontend/   # React application
```

## Prerequisites

- Java 17 or higher
- Node.js 16 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (for containerized deployment)

## Running the Applications

### Backend (Spring Boot)

1. Navigate to the backend directory:
   ```bash
   cd backend/app
   ```

2. Build the application:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The backend server will start at `http://localhost:8080`

### Frontend (React)

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   npm start
   ```

The frontend application will be available at `http://localhost:3000`

## Running Tests

### Backend Tests

To run the backend tests, navigate to the backend directory and execute:

```bash
cd backend/app
mvn test
```

This will run all unit and integration tests.

## API Documentation

The API documentation is available through Swagger UI when the backend is running:

- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Docker Deployment

To run the entire application using Docker:

1. Make sure Docker and Docker Compose are installed on your system
2. From the root directory, build and start the containers:

```bash
docker-compose up --build
```

This will:
- Build and start the backend container
- Build and start the frontend container
- Set up the necessary network connections

The application will be available at:
- Frontend: `http://localhost:3000`
- Backend: `http://localhost:8080`

## Features

- User Authentication (Register/Login)
- Create, Read, Update, and Delete Todos
- Mark Todos as Complete/Incomplete
- Filter and Search Todos
- Responsive Design

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
