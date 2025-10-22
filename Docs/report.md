
[TOP COVER]

<br><br><br><br><br><br>

<center>
<h1>Note0 - A Simple Note Sharing Application</h1>
</center>

<br><br><br><br><br><br>

[TITLE PAGE]

<br><br><br><br><br><br>

<center>
<h1>Note0 - A Simple Note Sharing Application</h1>
<h2>Project Report</h2>
</center>

<br><br><br><br><br><br>

[CERTIFICATION PAGE]

<br><br><br><br><br><br>

<center>
<h2>CERTIFICATE</h2>
</center>
<p>This is to certify that the project report entitled "Note0 - A Simple Note Sharing Application" is a bonafide record of the work done by [Your Name] under the guidance of [Guide Name].</p>

<br><br><br><br><br><br>

[ACKNOWLEDGEMENT]

<br><br><br><br><br><br>

<center>
<h2>ACKNOWLEDGEMENT</h2>
</center>
<p>I would like to express my special thanks of gratitude to my teacher [Teacher Name] as well as our principal [Principal Name] who gave me the golden opportunity to do this wonderful project on the topic "Note0 - A Simple Note Sharing Application", which also helped me in doing a lot of Research and i came to know about so many new things I am really thankful to them.
Secondly i would also like to thank my parents and friends who helped me a lot in finalizing this project within the limited time frame.</p>

<br><br><br><br><br><br>

[ABSTRACT]

<br><br><br><br><br><br>

<center>
<h2>ABSTRACT</h2>
</center>
<p>The Note0 project is a desktop-based note-sharing application developed in Java using the Swing framework. It provides a platform for users to register, log in, and share educational materials. The application follows a client-server architecture, with the Java Swing application acting as the client and a PostgreSQL database as the server. Key features include secure user authentication with password hashing, a role-based access control system (distinguishing between regular users and administrators), and a feed for browsing and managing shared materials. The application also integrates with the Cloudinary service for cloud-based file storage, allowing users to upload and share files seamlessly. The user interface is designed with FlatLaf for a modern look and feel, and the backend is supported by a robust database schema with features like material approval status. The project is built and managed using Apache Maven.</p>

<br><br><br><br><br><br>

[TABLE OF CONTENTS]

<br><br><br><br><br><br>

<center>
<h2>TABLE OF CONTENTS</h2>
</center>

<pre>
Chapter 1: Introduction
    1.1 Project Overview
    1.2 Project Goals and Objectives
Chapter 2: System Architecture
    2.1 High-Level Architecture
    2.2 Component Diagram
    2.3 Flowchart
Chapter 3: Component Analysis
    3.1 UI Components
    3.2 Data Access Objects (DAOs)
    3.3 Services
Chapter 4: Database Schema
    4.1 ER Diagram
    4.2 Table Descriptions
Chapter 5: File-by-File Analysis
    5.1 com.note0.simple.Main
    5.2 com.note0.simple.MainFrame
    5.3 com.note0.simple.WelcomeForm
    5.4 com.note0.simple.LoginForm & com.note0.simple.LoginPanel
    5.5 com.note0.simple.RegistrationForm & com.note0.simple.RegistrationPanel
    5.6 com.note0.simple.DashboardPanel
    5.7 com.note0.simple.FeedPanel
    5.8 com.note0.simple.ProfilePanel
    5.9 com.note0.simple.AdminPanel & com.note0.simple.AdminForm
    5.10 com.note0.simple.DatabaseManager
    5.11 com.note0.simple.UserDAO
    5.12 com.note0.simple.MaterialDAO
    5.13 com.note0.simple.SubjectDAO
    5.14 com.note0.simple.CloudinaryService
    5.15 Data Models (User, Subject, Material)
Chapter 6: Key Features
    6.1 User Authentication
    6.2 Material Sharing
    6.3 Admin Panel
Chapter 7: Security Analysis
    7.1 Password Hashing
    7.2 Role-Based Access Control
Chapter 8: Dependencies
    8.1 Maven Dependencies
Chapter 9: Conclusion
    9.1 Summary
    9.2 Future Work
</pre>

<br><br><br><br><br><br>

[LIST OF FIGURES AND TABLES]

<br><br><br><br><br><br>

<center>
<h2>LIST OF FIGURES AND TABLES</h2>
</center>

**Figures:**
* Figure 1: System Architecture Diagram
* Figure 2: Application Flowchart
* Figure 3: ER Diagram
* Figure 4: Login Screen Screenshot
* Figure 5: Dashboard Screenshot
* Figure 6: Admin Panel Screenshot

**Tables:**
* Table 1: Users Table
* Table 2: Subjects Table
* Table 3: Materials Table

<br><br><br><br><br><br>

[CHAPTERS]

<br><br><br><br><br><br>

<center>
<h1>Chapter 1: Introduction</h1>
</center>

<h2>1.1 Project Overview</h2>
<p style="text-align: justify; line-height: 1.5;">The Note0 project is a desktop-based note-sharing application designed for students and educators. It provides a simple and efficient platform for sharing and accessing educational materials. Built using Java Swing for the frontend and backed by a PostgreSQL database, the application offers a robust and scalable solution for academic collaboration. The project emphasizes security, usability, and a modern user experience through the integration of various third-party libraries.</p>

<h2>1.2 Project Goals and Objectives</h2>
<p style="text-align: justify; line-height: 1.5;">The primary goal of the Note0 project is to create a centralized platform for note sharing. The key objectives are:</p>
<ul>
    <li>To develop a secure user authentication system.</li>
    <li>To enable users to upload, view, and manage educational materials.</li>
    <li>To provide an administrative interface for user and content management.</li>
    <li>To ensure data integrity and security through proper database design and security practices.</li>
    <li>To deliver a modern and intuitive user interface.</li>
</ul>

<br><br><br>

<center>
<h1>Chapter 2: System Architecture</h1>
</center>

<h2>2.1 High-Level Architecture</h2>
<p style="text-align: justify; line-height: 1.5;">The application follows a two-tier client-server architecture. The Java Swing application serves as the client, providing the user interface and business logic. The PostgreSQL database acts as the server, storing all application data. The application also interacts with the external Cloudinary service for file storage.</p>

<h2>2.2 Component Diagram</h2>
<p style="text-align: justify; line-height: 1.5;"><b>Figure 1: System Architecture Diagram</b></p>
<center>
<img src="[PLACEHOLDER_FOR_SYSTEM_ARCHITECTURE_DIAGRAM]" alt="System Architecture Diagram">
</center>
<p style="text-align: justify; line-height: 1.5;">The diagram above illustrates the interaction between the Java client, the PostgreSQL database, and the Cloudinary service.</p>

<h2>2.3 Flowchart</h2>
<p style="text-align: justify; line-height: 1.5;"><b>Figure 2: Application Flowchart</b></p>
<center>
<img src="[PLACEHOLDER_FOR_APPLICATION_FLOWCHART]" alt="Application Flowchart">
</center>
<p style="text-align: justify; line-height: 1.5;">The flowchart shows the user's journey through the application, from login/registration to accessing the dashboard and interacting with the application's features.</p>


<br><br><br>

<center>
<h1>Chapter 3: Component Analysis</h1>
</center>

<h2>3.1 UI Components</h2>
<p style="text-align: justify; line-height: 1.5;">The user interface is built using Java Swing and is organized into several panels, each responsible for a specific functionality:</p>
<ul>
    <li><b>MainFrame:</b> The main window of the application that holds all other UI components.</li>
    <li><b>WelcomeForm/LoginPanel/RegistrationPanel:</b> Components responsible for the initial user interaction, including login and registration. The LoginPanel authenticates users, and the RegistrationPanel allows new users to create an account.</li>
</ul>
<p style="text-align: justify; line-height: 1.5;"><b>Figure 4: Login Screen Screenshot</b></p>
<center>
<img src="[PLACEHOLDER_FOR_LOGIN_SCREEN_SCREENSHOT]" alt="Login Screen Screenshot">
</center>
<ul>
    <li><b>DashboardPanel:</b> The main dashboard displayed after a user logs in. It provides navigation to the Feed, Profile, and Admin panels.</li>
</ul>
<p style="text-align: justify; line-height: 1.5;"><b>Figure 5: Dashboard Screenshot</b></p>
<center>
<img src="[PLACEHOLDER_FOR_DASHBOARD_SCREENSHOT]" alt="Dashboard Screenshot">
</center>
<ul>
    <li><b>FeedPanel:</b> Displays a feed of shared materials, allowing users to browse and upload new materials.</li>
    <li><b>ProfilePanel:</b> Allows users to view and manage their profile.</li>
    <li><b>AdminPanel:</b> Provides administrative functionalities for user management, including user verification and material approval.</li>
</ul>
<p style="text-align: justify; line-height: 1.5;"><b>Figure 6: Admin Panel Screenshot</b></p>
<center>
<img src="[PLACEHOLDER_FOR_ADMIN_PANEL_SCREENSHOT]" alt="Admin Panel Screenshot">
</center>


<h2>3.2 Data Access Objects (DAOs)</h2>
<p style="text-align: justify; line-height: 1.5;">Data persistence is managed by a set of Data Access Objects (DAOs):</p>
<ul>
    <li><b>UserDAO:</b> Handles all database operations related to users, such as creating, retrieving, and updating user information. It includes methods for user registration, login with password verification (using jBCrypt), and fetching all users for the admin panel.</li>
    <li><b>SubjectDAO:</b> Manages the subjects available in the application.</li>
    <li><b>MaterialDAO:</b> Responsible for database operations related to materials, including creation, retrieval, and updates. It fetches all materials for the feed and allows for material approval by admins.</li>
</ul>

<h2>3.3 Services</h2>
<p style="text-align: justify; line-height: 1.5;">The application uses a service for interacting with the Cloudinary API:</p>
<ul>
    <li><b>CloudinaryService:</b> Provides methods for uploading files to the Cloudinary cloud storage. It handles the connection to the Cloudinary API and manages the file upload process, returning the URL of the uploaded file.</li>
</ul>

<br><br><br>

<center>
<h1>Chapter 4: Database Schema</h1>
</center>

<h2>4.1 ER Diagram</h2>
<p style="text-align: justify; line-height: 1.5;"><b>Figure 3: ER Diagram</b></p>
<center>
<img src="[PLACEHOLDER_FOR_ER_DIAGRAM]" alt="ER Diagram">
</center>
<p style="text-align: justify; line-height: 1.5;">The ER diagram shows the relationships between the \`users\`, \`subjects\`, and \`materials\` tables.</p>

<h2>4.2 Table Descriptions</h2>
<p style="text-align: justify; line-height: 1.5;"><b>Table 1: Users Table</b></p>
<table border="1" style="width:100%; border-collapse: collapse;">
    <tr>
        <th>Column Name</th>
        <th>Data Type</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>id</td>
        <td>SERIAL</td>
        <td>Primary Key</td>
    </tr>
    <tr>
        <td>full_name</td>
        <td>VARCHAR</td>
        <td>Full name of the user</td>
    </tr>
    <tr>
        <td>email</td>
        <td>VARCHAR</td>
        <td>Email of the user (unique)</td>
    </tr>
    <tr>
        <td>password_hash</td>
        <td>VARCHAR</td>
        <td>Hashed password</td>
    </tr>
    <tr>
        <td>role</td>
        <td>VARCHAR</td>
        <td>Role of the user (e.g., USER, ADMIN)</td>
    </tr>
    <tr>
        <td>is_active</td>
        <td>BOOLEAN</td>
        <td>Indicates if the user account is active</td>
    </tr>
    <tr>
        <td>is_verified</td>
        <td>BOOLEAN</td>
        <td>Indicates if the user is verified</td>
    </tr>
    <tr>
        <td>college_name</td>
        <td>VARCHAR</td>
        <td>Name of the user\'s college</td>
    </tr>
    <tr>
        <td>semester</td>
        <td>INTEGER</td>
        <td>Current semester of the user</td>
    </tr>
</table>
<br>
<p style="text-align: justify; line-height: 1.5;"><b>Table 2: Subjects Table</b></p>
<table border="1" style="width:100%; border-collapse: collapse;">
    <tr>
        <th>Column Name</th>
        <th>Data Type</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>id</td>
        <td>SERIAL</td>
        <td>Primary Key</td>
    </tr>
    <tr>
        <td>name</td>
        <td>VARCHAR</td>
        <td>Name of the subject</td>
    </tr>
</table>
<br>
<p style="text-align: justify; line-height: 1.5;"><b>Table 3: Materials Table</b></p>
<table border="1" style="width:100%; border-collapse: collapse;">
    <tr>
        <th>Column Name</th>
        <th>Data Type</th>
        <th>Description</th>
    </tr>
    <tr>
        <td>id</td>
        <td>SERIAL</td>
        <td>Primary Key</td>
    </tr>
    <tr>
        <td>title</td>
        <td>VARCHAR</td>
        <td>Title of the material</td>
    </tr>
    <tr>
        <td>description</td>
        <td>TEXT</td>
        <td>Description of the material</td>
    </tr>
    <tr>
        <td>file_type</td>
        <td>VARCHAR</td>
        <td>Type of the file (e.g., PDF, DOCX)</td>
    </tr>
    <tr>
        <td>file_path</td>
        <td>VARCHAR</td>
        <td>URL or path to the file</td>
    </tr>
    <tr>
        <td>module_number</td>
        <td>INTEGER</td>
        <td>Module number the material belongs to</td>
    </tr>
    <tr>
        <td>uploader_id</td>
        <td>INTEGER</td>
        <td>Foreign key to the users table</td>
    </tr>
    <tr>
        <td>subject_id</td>
        <td>INTEGER</td>
        <td>Foreign key to the subjects table</td>
    </tr>
    <tr>
        <td>avg_rating</td>
        <td>NUMERIC</td>
        <td>Average rating of the material</td>
    </tr>
    <tr>
        <td>upload_date</td>
        <td>TIMESTAMP</td>
        <td>Date and time of upload</td>
    </tr>
    <tr>
        <td>approval_status</td>
        <td>VARCHAR</td>
        <td>Approval status of the material (e.g., PENDING, APPROVED)</td>
    </tr>
</table>

<br><br><br>
<center>
<h1>Chapter 5: File-by-File Analysis</h1>
</center>

<h2>5.1 com.note0.simple.Main</h2>
<p style="text-align: justify; line-height: 1.5;">This is the entry point of the application. Its `main` method uses `SwingUtilities.invokeLater` to ensure that the GUI is created on the Event Dispatch Thread (EDT), which is essential for a stable Swing application. It first creates an instance of `UserDAO` to ensure the admin user exists, and then it creates and displays the `MainFrame`.</p>

<h2>5.2 com.note0.simple.MainFrame</h2>
<p style="text-align: justify; line-height: 1.5;">This class extends `JFrame` and serves as the main window of the application. It manages the currently logged-in user and handles the switching between different panels (e.g., `WelcomeForm`, `DashboardPanel`). It holds the central logic for navigating through the application.</p>

<h2>5.3 com.note0.simple.WelcomeForm</h2>
<p style="text-align: justify; line-height: 1.5;">This is the initial screen that users see. It presents two options: "Login" and "Register". It acts as a simple navigational panel to either the `LoginPanel` or the `RegistrationPanel`.</p>

<h2>5.4 com.note0.simple.LoginForm & com.note0.simple.LoginPanel</h2>
<p style="text-align: justify; line-height: 1.5;">The `LoginPanel` provides the GUI for the login form, including fields for email and password. The `LoginForm` (which is likely the panel itself or a class that uses it) contains the logic for authenticating the user. It uses the `UserDAO` to verify the user's credentials. If the credentials are valid, it notifies the `MainFrame` to switch to the `DashboardPanel`.</p>

<h2>5.5 com.note0.simple.RegistrationForm & com.note0.simple.RegistrationPanel</h2>
<p style="text-align: justify; line-height: 1.5;">Similar to the login components, the `RegistrationPanel` provides the form for new users to register. The `RegistrationForm` collects user information (full name, email, password, etc.) and uses the `UserDAO` to create a new user in the database. Passwords are hashed using `jBCrypt` before being stored.</p>

<h2>5.6 com.note0.simple.DashboardPanel</h2>
<p style="text-align: justify; line-height: 1.5;">This panel is the main hub for logged-in users. It typically contains navigation elements to access the different features of the application, such as the `FeedPanel`, `ProfilePanel`, and, if the user is an admin, the `AdminPanel`.</p>

<h2>5.7 com.note0.simple.FeedPanel</h2>
<p style="text-align: justify; line-height: 1.5;">The `FeedPanel` is responsible for displaying the list of shared materials. It uses the `MaterialDAO` to fetch the materials from the database and displays them in a user-friendly format, likely a list or a grid. It also includes functionality for uploading new materials, which involves file selection and interaction with the `CloudinaryService`.</p>

<h2>5.8 com.note0.simple.ProfilePanel</h2>
<p style="text-align: justify; line-height: 1.5;">This panel allows users to view their own information, such as their name, email, and college. It retrieves the user's data from the `User` object stored in the `MainFrame`.</p>

<h2>5.9 com.note0.simple.AdminPanel & com.note0.simple.AdminForm</h2>
<p style="text-align: justify; line-height: 1.5;">These components are only accessible to administrators. The `AdminPanel` displays a list of users and unapproved materials, fetched using the `UserDAO` and `MaterialDAO`. The `AdminForm` provides the interface for an admin to take actions, such as verifying users or approving materials.</p>

<h2>5.10 com.note0.simple.DatabaseManager</h2>
<p style="text-align: justify; line-height: 1.5;">This class is responsible for managing the connection to the PostgreSQL database. It reads the database credentials from environment variables and provides a static method to get a database connection, which is used by all the DAOs.</p>

<h2>5.11 com.note0.simple.UserDAO</h2>
<p style="text-align: justify; line-height: 1.5;">The `UserDAO` (Data Access Object) handles all database operations related to users. It contains methods for creating a new user, finding a user by email, and verifying a user's password using `jBCrypt`. It also has a method to create the initial admin user if one doesn't exist.</p>

<h2>5.12 com.note0.simple.MaterialDAO</h2>
<p style="text-align: justify; line-height: 1.5;">This DAO is responsible for all database operations related to materials. It has methods to insert a new material into the database, retrieve all materials, and update the approval status of a material.</p>

<h2>5.13 com.note0.simple.SubjectDAO</h2>
<p style="text-align: justify; line-height: 1.5;">The `SubjectDAO` manages the subjects in the database. It contains a method to retrieve all subjects, which are then used in the `FeedPanel` when a user uploads a new material.</p>

<h2>5.14 com.note0.simple.CloudinaryService</h2>
<p style="text-align: justify; line-height: 1.5;">This service class encapsulates the logic for interacting with the Cloudinary API. It has a method that takes a file path as input, uploads the file to Cloudinary, and returns the public URL of the uploaded file.</p>

<h2>5.15 Data Models (User, Subject, Material)</h2>
<p style="text-align: justify; line-height: 1.5;">These are plain old Java objects (POJOs) that represent the data structures of the application. They have fields that correspond to the columns in the database tables and are used to pass data between the different layers of the application.</p>


<br><br><br>
<center>
<h1>Chapter 6: Key Features</h1>
</center>

<h2>6.1 User Authentication</h2>
<p style="text-align: justify; line-height: 1.5;">The application implements a secure user authentication system. New users can register by providing their details, and their passwords are automatically hashed using the jBCrypt library before being stored in the database. Existing users can log in with their email and password, and the application verifies their credentials against the stored hashed password.</p>

<h2>6.2 Material Sharing</h2>
<p style="text-align: justify; line-height: 1.5;">Once logged in, users can share educational materials. The application allows users to upload files, which are then stored in the Cloudinary cloud storage. The metadata of the uploaded material, such as the title, description, and subject, is stored in the PostgreSQL database. Users can view a feed of shared materials and access the uploaded files.</p>

<h2>6.3 Admin Panel</h2>
<p style="text-align: justify; line-height: 1.5;">The application includes a dedicated admin panel for administrative tasks. The admin panel is accessible only to users with the \'ADMIN\' role. It provides functionalities for managing users, such as viewing all registered users and approving new materials.</p>

<br><br><br>

<center>
<h1>Chapter 7: Security Analysis</h1>
</center>

<h2>7.1 Password Hashing</h2>
<p style="text-align: justify; line-height: 1.5;">To enhance security, the application does not store passwords in plain text. Instead, it uses the jBCrypt library to hash passwords before storing them in the database. When a user attempts to log in, the provided password is hashed and then compared with the stored hash, preventing unauthorized access even if the database is compromised.</p>

<h2>7.2 Role-Based Access Control</h2>
<p style="text-align: justify; line-height: 1.5;">The application implements a role-based access control (RBAC) system to manage user permissions. There are two roles: \'USER\' and \'ADMIN\'. Regular users have access to the standard features of the application, such as sharing and viewing materials. Administrators have elevated privileges, including access to the admin panel for user and content management.</p>

<br><br><br>

<center>
<h1>Chapter 8: Dependencies</h1>
</center>

<h2>8.1 Maven Dependencies</h2>
<p style="text-align: justify; line-height: 1.5;">The project uses Apache Maven for dependency management. The key dependencies are:</p>
<ul>
    <li><b>postgresql:</b> For connecting to the PostgreSQL database.</li>
    <li><b>jbcrypt:</b> For hashing user passwords.</li>
    <li><b>cloudinary-http44:</b> For integrating with the Cloudinary file upload service.</li>
    <li><b>flatlaf:</b> For a modern look and feel for the Swing user interface.</li>
</ul>

<br><br><br>

<center>
<h1>Chapter 9: Conclusion</h1>
</center>

<h2>9.1 Summary</h2>
<p style="text-align: justify; line-height: 1.5;">The Note0 project is a well-rounded desktop application for note sharing. It successfully integrates a Java Swing frontend with a PostgreSQL backend and a cloud-based file storage service. The application prioritizes security through password hashing and role-based access control, and it provides a user-friendly interface for a seamless user experience.</p>

<h2>9.2 Future Work</h2>
<p style="text-align: justify; line-height: 1.5;">Future enhancements for the project could include:</p>
<ul>
    <li>Implementing a search functionality for materials.</li>
    <li>Adding a rating and commenting system for materials.</li>
    <li>Developing a web-based version of the application.</li>
    <li>Adding support for more file types.</li>
</ul>

<br><br><br>

[APPENDICES]

<br><br><br><br><br><br>

[REFERENCES/BIBLIOGRAPHY]
