//package com.example.application_management.project.controller;
//
//import com.example.application_management.project.dto.ProjectRequest;
//import com.example.application_management.project.dto.ProjectResponse;
//import com.example.application_management.project.entity.Project;
//import com.example.application_management.project.service.ProjectService;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("api/projects")
//public class ProjectController {
//
//    private final ProjectService projectService;
//
//    public ProjectController(ProjectService projectService) {
//        this.projectService = projectService;
//    }
//
////    @GetMapping
////    public List<Project> getProjects() {
////        return projectService.getAllProjects();
////    }
//
//    @GetMapping
//    public ResponseEntity<List<ProjectResponse>> getProjects() {
//        List<ProjectResponse> projects = projectService.getAllProject();
//
//        return ResponseEntity.ok(projects);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
//        ProjectResponse project = projectService.getProjectById(id);
//
//        return ResponseEntity.ok(project);
//    }
//
//    @PostMapping
//    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
//        ProjectResponse createdProject = projectService.createProject(request);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
//        ProjectResponse updatedProject = projectService.updateProject(id, request);
//
//        return ResponseEntity.ok(updatedProject);
//    }
//
//    @PostMapping("/{id}/activate")
//    public ResponseEntity<ProjectResponse> activateProject(@PathVariable Long id) {
//
//        ProjectResponse project = projectService.activateProject(id);
//
//        return ResponseEntity.ok(project);
//    }
//
//    @PostMapping("/{id}/complete")
//    public ResponseEntity<ProjectResponse> completeProject(@PathVariable Long id) {
//
//        ProjectResponse project = projectService.completeProject(id);
//
//        return ResponseEntity.ok(project);
//    }
//}





/// ////////////////////////////////////////////


package com.example.application_management.project.controller;

// DTO untuk menerima data daripada client semasa create/update project.
import com.example.application_management.project.dto.ProjectRequest;

// DTO untuk menghantar data project kepada client sebagai JSON response.
import com.example.application_management.project.dto.ProjectResponse;

// Service layer yang mengendalikan business logic berkaitan project.
import com.example.application_management.project.service.ProjectService;

// Digunakan untuk menjalankan validation terhadap request body.
// Contohnya @NotBlank, @NotNull, @Size dalam ProjectRequest.
import jakarta.validation.Valid;

// Menyediakan HTTP status code seperti 200 OK dan 201 CREATED.
import org.springframework.http.HttpStatus;

// Membolehkan kita mengawal HTTP status dan response body.
import org.springframework.http.ResponseEntity;

// Spring REST annotations seperti:
// @RestController, @RequestMapping, @GetMapping,
// @PostMapping, @PutMapping, @PathVariable dan @RequestBody.
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProjectController
 *
 * REST Controller untuk menguruskan Project dalam
 * Engineering Project Management System (EPMS).
 *
 * Tanggungjawab Controller:
 *
 * 1. Menerima HTTP request daripada client
 *    seperti Angular frontend, Postman atau sistem lain.
 *
 * 2. Membaca input daripada URL path dan request body.
 *
 * 3. Menjalankan request validation melalui @Valid.
 *
 * 4. Memanggil ProjectService untuk menjalankan business logic.
 *
 * 5. Mengembalikan HTTP response kepada client.
 *
 * Controller tidak sepatutnya mengandungi database query
 * atau business logic yang kompleks.
 *
 * Application flow:
 *
 * Angular / Postman
 *       |
 *       v
 * ProjectController
 *       |
 *       v
 * ProjectService
 *       |
 *       v
 * ProjectRepository
 *       |
 *       v
 * Database
 */
@RestController

// Base URL untuk semua endpoint dalam controller ini.
//
// Contoh:
// GET  /api/projects
// POST /api/projects
// GET  /api/projects/1
@RequestMapping("/api/projects")
public class ProjectController {

    /**
     * Dependency kepada ProjectService.
     *
     * final bermaksud reference ini tidak boleh
     * ditukar selepas constructor selesai.
     */
    private final ProjectService projectService;

    /**
     * Constructor Injection.
     *
     * Spring akan inject ProjectService secara automatik
     * apabila ProjectController dicipta.
     *
     * Ini ialah konsep Dependency Injection (DI).
     *
     * Kita tidak perlu menggunakan:
     *
     * new ProjectService(...)
     *
     * kerana Spring menguruskan dependency tersebut.
     *
     * @param projectService service untuk project management
     */
    public ProjectController(ProjectService projectService) {

        this.projectService = projectService;

    }

    /**
     * GET ALL PROJECTS
     *
     * Endpoint:
     * GET /api/projects
     *
     * Tujuan:
     * Mendapatkan senarai semua projects daripada database.
     *
     * Flow:
     *
     * 1. Client menghantar HTTP GET request.
     * 2. Controller memanggil projectService.getAllProject().
     * 3. Service mendapatkan data project.
     * 4. Service memulangkan List<ProjectResponse>.
     * 5. Controller mengembalikan HTTP 200 OK bersama JSON array.
     *
     * Contoh request:
     *
     * GET http://localhost:8080/api/projects
     *
     * Expected HTTP response:
     *
     * Status: 200 OK
     *
     * Body:
     * [
     *   { ... project response ... },
     *   { ... project response ... }
     * ]
     *
     * Jika tiada project, response biasanya berupa
     * empty array [] dengan HTTP 200 OK.
     *
     * @return ResponseEntity yang mengandungi
     *         List<ProjectResponse>
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects() {

        // Panggil Service Layer untuk mendapatkan semua projects.
        //
        // Controller tidak menjalankan database query secara terus.
        List<ProjectResponse> projects =
                projectService.getAllProject();

        // ResponseEntity.ok() menghasilkan HTTP 200 OK.
        //
        // Spring Boot akan serialize List<ProjectResponse>
        // kepada JSON menggunakan Jackson.
        return ResponseEntity.ok(projects);

    }

    /**
     * GET PROJECT BY ID
     *
     * Endpoint:
     * GET /api/projects/{id}
     *
     * Tujuan:
     * Mendapatkan maklumat satu project berdasarkan ID.
     *
     * Contoh request:
     *
     * GET http://localhost:8080/api/projects/1
     *
     * @PathVariable:
     * Mengambil nilai daripada URL dan memasukkannya
     * ke dalam parameter method.
     *
     * Contoh:
     * /api/projects/1
     *
     * id = 1
     *
     * Flow:
     *
     * 1. Client menghantar project ID dalam URL.
     * 2. Controller menerima ID melalui @PathVariable.
     * 3. Service mencari project berdasarkan ID.
     * 4. Controller mengembalikan ProjectResponse.
     *
     * Expected HTTP response:
     *
     * Status: 200 OK
     *
     * Jika project tidak wujud, status error bergantung
     * kepada exception handling dalam Service dan
     * GlobalExceptionHandler.
     *
     * @param id unique ID project
     * @return ProjectResponse bagi project tersebut
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id) {

        // Dapatkan project berdasarkan ID.
        ProjectResponse project =
                projectService.getProjectById(id);

        // Return HTTP 200 OK bersama project data.
        return ResponseEntity.ok(project);

    }

    /**
     * CREATE NEW PROJECT
     *
     * Endpoint:
     * POST /api/projects
     *
     * Tujuan:
     * Mencipta project baru.
     *
     * Contoh request:
     *
     * POST http://localhost:8080/api/projects
     *
     * Content-Type: application/json
     *
     * Request body:
     * {
     *   ... fields daripada ProjectRequest ...
     * }
     *
     * @RequestBody:
     * Spring Boot akan deserialize JSON request body
     * kepada Java object ProjectRequest.
     *
     * @Valid:
     * Menjalankan Bean Validation berdasarkan annotations
     * yang ditetapkan dalam ProjectRequest.
     *
     * Contohnya:
     * @NotBlank
     * @NotNull
     * @Size
     *
     * Jika validation gagal, Spring biasanya menghasilkan
     * HTTP 400 Bad Request.
     *
     * Flow:
     *
     * 1. Client menghantar HTTP POST bersama JSON.
     * 2. Spring convert JSON kepada ProjectRequest.
     * 3. @Valid menjalankan request validation.
     * 4. Controller memanggil createProject().
     * 5. Service menjalankan business logic dan menyimpan project.
     * 6. Controller mengembalikan HTTP 201 CREATED.
     *
     * @param request data untuk mencipta project
     * @return project yang telah berjaya dicipta
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request) {

        // Hantar request DTO kepada Service Layer.
        ProjectResponse createdProject =
                projectService.createProject(request);

        // HTTP 201 CREATED digunakan apabila resource baru
        // berjaya dicipta.
        //
        // .body() mengandungi project yang telah dicipta.
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdProject);

    }

    /**
     * UPDATE EXISTING PROJECT
     *
     * Endpoint:
     * PUT /api/projects/{id}
     *
     * Tujuan:
     * Mengemas kini maklumat project berdasarkan ID.
     *
     * Contoh request:
     *
     * PUT http://localhost:8080/api/projects/1
     *
     * Content-Type: application/json
     *
     * Request body:
     * {
     *   ... fields daripada ProjectRequest ...
     * }
     *
     * @PathVariable:
     * Mengambil ID project daripada URL.
     *
     * @RequestBody:
     * Convert JSON request kepada ProjectRequest.
     *
     * @Valid:
     * Menjalankan validation sebelum memanggil Service.
     *
     * Flow:
     *
     * 1. Client menghantar project ID dan JSON.
     * 2. Spring menjalankan request validation.
     * 3. Controller memanggil updateProject().
     * 4. Service mengemas kini project.
     * 5. Controller mengembalikan updated ProjectResponse.
     *
     * Nota:
     * HTTP PUT lazimnya digunakan untuk menggantikan
     * representasi resource, tetapi behavior sebenar
     * bergantung kepada implementasi ProjectService.
     *
     * @param id unique ID project
     * @param request data project yang hendak dikemas kini
     * @return updated ProjectResponse
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {

        // Panggil service untuk update project.
        ProjectResponse updatedProject =
                projectService.updateProject(id, request);

        // Return HTTP 200 OK bersama updated project.
        return ResponseEntity.ok(updatedProject);

    }

    /**
     * ACTIVATE PROJECT
     *
     * Endpoint:
     * POST /api/projects/{id}/activate
     *
     * Tujuan:
     * Mengaktifkan project tertentu.
     *
     * Contoh request:
     *
     * POST http://localhost:8080/api/projects/1/activate
     *
     * Endpoint ini mewakili business action,
     * bukan sekadar update field biasa.
     *
     * Contoh business scenario:
     *
     * Project baru berada dalam status PLANNING.
     * Selepas project diluluskan untuk bermula,
     * user menjalankan Activate Project.
     *
     * PLANNING --> ACTIVE
     *
     * Nota:
     * Status transition sebenar dan validation
     * ditentukan oleh ProjectService.activateProject().
     *
     * Contohnya, service boleh menolak activation
     * jika status project tidak membenarkannya.
     *
     * Flow:
     *
     * 1. Client menghantar HTTP POST.
     * 2. Controller menerima project ID.
     * 3. Service menjalankan activation logic.
     * 4. Controller return updated ProjectResponse.
     *
     * @param id unique ID project
     * @return ProjectResponse selepas activation
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<ProjectResponse> activateProject(
            @PathVariable Long id) {

        // Business logic activation berada dalam Service Layer.
        ProjectResponse project =
                projectService.activateProject(id);

        // Return HTTP 200 OK bersama project status terkini.
        return ResponseEntity.ok(project);

    }

    /**
     * COMPLETE PROJECT
     *
     * Endpoint:
     * POST /api/projects/{id}/complete
     *
     * Tujuan:
     * Menandakan project sebagai completed.
     *
     * Contoh request:
     *
     * POST http://localhost:8080/api/projects/1/complete
     *
     * Contoh business scenario:
     *
     * Project sedang berjalan dalam status ACTIVE.
     * Semua kerja telah selesai.
     * User menjalankan Complete Project.
     *
     * ACTIVE --> COMPLETED
     *
     * Nota:
     * Service bertanggungjawab memastikan status transition
     * tersebut dibenarkan mengikut business rules.
     *
     * Flow:
     *
     * 1. Client menghantar HTTP POST.
     * 2. Controller menerima project ID.
     * 3. Service menjalankan completion logic.
     * 4. Controller return updated ProjectResponse.
     *
     * @param id unique ID project
     * @return ProjectResponse selepas completion
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<ProjectResponse> completeProject(
            @PathVariable Long id) {

        // Panggil Service Layer untuk complete project.
        ProjectResponse project =
                projectService.completeProject(id);

        // Return HTTP 200 OK bersama project status terkini.
        return ResponseEntity.ok(project);

    }

}

