# SkillSwap
SkillSwap is a one stop solution for those who need help and those who want to help(Skill barter). Our site offers variety of features to enable the neighbours connect easily and find help within few clicks.

## Dependencies
A number of dependencies used during the development of the site are as follows:
### NextJs
We developed the frontend for the SkillSwap using next Js. The NextJs is built on top of ReactJs. 

To start with NextJs you need to make sure that you have node installed in your machine. To install **Node** you need to run the following command.
```bash
sudo apt-get update  
sudo apt-get install nodejs
```

#### To install NextJs:
```bash
npm install next@latest react@latest react-dom@latest
```


### Spring boot
The backend for the Skillswap was developed using Spring Boot, which is a Java framework.

To start a project in Spring you require Java Development Kit in your machine.
To install **JDK** run:
```bash
sudo apt-get update  
sudo apt-get install openjdk-17-jdk openjdk-17-jre
```

You also require **Maven** in the system to build the project. Run the following command to install maven.
```bash
sudo apt-get install maven
```

### External Dependencies
#### Frontend Dependencies 
- **Tailwind CSS** - To make the process of designing the page easier.
- **Openstreetmap Api** - To convert location to latitude and longitude
- **Upstash Redis** - To store chats
- **Pusher** - To get real time updates in chat

There are many other front end dependencies and all these can be installed with the following command:
```bash
cd skillswap-frontend
npm install
```
#### Backend Dependencies 
- **Mapstruct** - To simplify the implementation of mappings between Java beans.
- **Lombok** - To simplify the implementation of setters and getters.
- **Jacoco** - To analyze the code coverage.
- **JUnit & Mockito** - To write the test cases for the backend.

Each dependency will be installed when we build the backend project using Maven.
```bash
cd skillswap
mvn clean install
```

 
## Build Documentation
The build and deployment of the project involves several steps to be followed. The steps are provided in the documentation below.
### Build Backend
Once all the dependencies are installed we need to build the project. 
To build the backend run the following commands in the project source root.
```bash
cd skillswap
mvn clean package -Dactive.profile=prod
```

Once it is completed you will see `skillswap-backend-0.0.1-SNAPSHOT.jar` in your target directory. 

To build the docker image for the backend run the following commands from the source root.
```bash
cd skillswap
docker build -t <ImageName> -f Dockerfile .
```
Make sure to replace the `<ImageName>` with the name you want to give to your container. 

### Build Frontend

After installing the frontend dependencies using the node packet manager we can build the front end static files using the following command from the root of frontend:
```bash
npm run build
```
This will generate a dist directory that includes the static file that can be served with a server. We used npm's server to serve the static pages.

To build the docker 

### Deploying the project
To deploy the project we used `gitlabCI` to automate the process of build and deploy. To create your own `gitlabCI` you will require a `gitlab runner`. You can follow the Gitlab's official documentation to [install the gitlab runner](https://docs.gitlab.com/runner/install/) on your system.

We included the following steps to build and deploy the project
-  **Test**: To run the test cases.
- **Build**: To build the backend package jar file.
- **Quality**:  To generate the code smells of the code using Designite Java.
- **Publish**: To push the docker image to docker hub
- **Deploy**: To deploy the application on the server.

Once your gitlab runner is ready, you can change the variables in the Gitlab's variables section inside pipeline settings.

Variables to update:
- **SERVER_IP** - The ipv4 address of the server where you want to deploy the application.
- **SERVER_USER** - The user inside the server.
- **DOCKER_USER** - The username on the docker hub where the images are stored.
- **DOCKER_PASSWORD** - The password for the user on docker hub.
- **ID_RSA** - This is a file that is generated to SSH in to the server.
- **IMAGE_NAME** - The name of the Backend docker image
- **FRONTEND_IMAGE_NAME** - The name of the frontend docker image.

After you configure these variables you can push you updated code to `main` branch and the pipeline will be activated and the site will be available on the server's ip address.

## User Scenerios

### Feature-1: Signin and Signup

#### Scenario 1: Signing In

- **User Goal:** Access SkillSwap to explore and connect with others.
- **Steps:**
  1.  Navigate to the Sign In page on SkillSwap.
  2.  Enter your username/email and password.
  3.  Click on the "Sign In" button.
- **Outcome:** Gain access to your SkillSwap account and be redirected to the Explore page to start discovering and sharing skills.
- **Screenshot:**
![UserReview](./docs/images/signin.png)
#### Scenario 2: Signing Up

- **User Goal:** Join the SkillSwap community and share your skills.
- **Steps:**
  1.  Visit the Sign Up page on SkillSwap.
  2.  Provide necessary information, including username, email, and password.
  3.  Press the "Sign Up" button.
  4.  Select your respective user skills when prompted.
- **Outcome:** Successfully become a member of SkillSwap, select your user skills, and be redirected to the Explore page to begin your skill-sharing journey.
- **Screenshot:**
![UserReview](./docs/images/signup.png)
#### Scenario 3: Password Recovery

- **User Goal:** Regain access to the SkillSwap account after forgetting the password.
- **Steps:**
  1.  Access the Forget Password feature on the SkillSwap platform.
  2.  Enter the email associated with your SkillSwap account.
  3.  Follow the instructions sent to your email for password recovery.
  4.  Set a new password as instructed to regain access to your account securely.
- **Outcome:** Successfully reset the password for your SkillSwap account and resume exploring and sharing skills within the community.
- **Screenshot:**
![UserReview](./docs/images/forgotpassword.png)
### Feature-2: Explore page

### Scenario 1: Exploring Posts

- **User Goal:** Discovering relevant posts from other users and engaging with the community.
- **Steps:**
  1.  **Navigation:** Navigate to the Explore page on SkillSwap.
  2.  **Select Posts Tab:** Click on the "Posts" tab to explore user-generated posts.
  3.  **View Posts:** Scroll through the feed of posts from other users, showcasing various skills, interests, and expertise.
  4.  **Profile Exploration:** Click on a post to view the author's profile and learn more about their background and skills.
  5.  **Initiate Chat:** If interested, initiate a chat with the author directly from the post to discuss shared interests or seek further guidance.
  6.  **Filter by Skill:** Utilize the skill filter option to narrow down the post feed and find posts relevant to specific skills of interest.
  7.  **Create Post:** If inclined, create a post by specifying the content and the skill you're seeking help with or offering assistance in, fostering interactions within the community.
- **Screenshot:**
![UserReview](./docs/images/posts.png)
### Scenario 2: Exploring Profiles

- **User Goal:** Connecting with users based on location and shared skills.
- **Steps:**
  1.  **Navigation:** Access the Explore page on SkillSwap.
  2.  **Select Profiles Tab:** Click on the "Profiles" tab to explore user profiles.
  3.  **Browse Profiles:** Slide through user profiles displayed based on location filters, observing their skills, location, and bio.
  4.  **Adjust Location Filter:** Customize the location filter to discover users in specific geographical areas, facilitating local networking and collaboration opportunities.
  5.  **Filter by Skill:** Refine the search by filtering profiles based on specific skills you're seeking or offering, connecting with users who align with your interests and expertise.
  6.  **Explore Further:** Click on a profile to delve deeper into the user's background, skills, and contributions to the community, fostering meaningful connections and collaborations.
- **Screenshot:**
![UserReview](./docs/images/profiles.png)
### Feature-3: User profile page

#### Scenario 1: Profile Management

- **User Goal:** Update profile details and manage posted content.
- **Steps:**
  1.  Navigate to the User Profile Page from the sidebar on the Explore page.
  2.  Select the "Profile Page" option or click on your name/photo in the sidebar.
  3.  Edit profile information such as name, bio, profile picture, contact details, and social media links.
  4.  Update or add skills to showcase your expertise.
  5.  View and manage posts, including deleting any posts as desired.
- **Outcome:** Successfully update profile details and manage posted content, ensuring your profile accurately reflects your skills and interests.
- **Screenshot:**
![UserReview](./docs/images/profilepage.png)

#### Scenario 2: Viewing Reviews

- **User Goal:** Gain insights into feedback provided by other users.
- **Steps:**
  1.  Visit the User Profile Page.
  2.  Navigate to the section displaying reviews or feedback.
  3.  Review feedback provided by other users.
- **Outcome:** Gain valuable insights into your contributions to the community and how other users perceive your skills and interactions.
- **Screenshot:**
![UserReview](./docs/images/reviews.png)
### Feature-4: Chat

#### Scenario 1: Starting a New Conversation

- **User Goal:** Initiate a conversation with another user on SkillSwap.
- **Steps:**
  1.  Navigate to the Explore page.
  2.  Browse through either the Posts or Profiles tab to find a user you'd like to chat with.
  3.  Click on the chat icon associated with the user's profile or post.
  4.  You will be redirected to the Chat section with the selected user added to the list of current conversations.
  5.  Begin typing your message and send it to start the conversation.
- **Outcome:** Successfully start a new conversation with another user, initiated from either the Posts or Profiles tab on the Explore page, and seamlessly continue the conversation in the Chat section.

#### Scenario 2: Accessing Conversations

- **User Goal:** Access and manage ongoing conversations with other users.
- **Steps:**
  1.  Visit the Chat section from the sidebar menu.
  2.  View the list of current conversations, including the names or usernames of the participants.
  3.  Click on a conversation to open it and continue the transaction of messages.
- **Outcome:** Easily access and navigate through ongoing conversations, staying connected with other users and staying informed about recent discussions.
- **Screenshot:**
![UserReview](./docs/images/chat.png)
### Feature-5: Notifications

#### Scenario 1: Managing Notification Settings

- **User Goal:** Customize notification preferences according to personal preferences and needs.
- **Steps:**
  1.  Navigate to the Settings section from the sidebar menu.
  2.  Access the Notification Settings option.
  3.  Enable or disable notifications for specific activities, such as new chat messages or post interactions.
  4.  Toggle notification sounds on or off based on your preference.
- **Outcome:** Successfully customize notification settings to receive alerts for relevant activities and configure notification sounds according to personal preference.
- **Screenshot:**
![UserReview](./docs/images/settings.png)
#### Scenario 2: Receiving Chat Notifications

- **User Goal:** Stay informed about new chat messages from other users.
- **Steps:**
  1.  Receive a new chat message from another user.
  2.  A notification icon appears next to the Profiles tab button on the Explore page, indicating a new notification.
  3.  Click on the notification icon to view the new chat message notification.
  4.  The notification displays the sender's name or username and a preview of the message.
  5.  Click on the notification to be redirected to the Chat section and view the full message.
- **Outcome:** Stay informed about new chat messages and easily access them by clicking on the notification icon on the Explore page, ensuring seamless communication with other members of the community.
**Screenshot:**
  ![UserReview](./docs/images/notification.png)


### Feature-6: User Feedback

#### Scenario 1: Writing and Deleting Feedback

- **User Goal:** Provide feedback on another user's profile and have the option to delete it later.
- **Steps:**
  1.  Navigate to the Explore page and view another user's profile.
  2.  Scroll down to the Feedback section.
  3.  Rate the user using the star rating system.
  4.  Write detailed feedback in the text field, sharing your thoughts and experiences.
  5.  Optionally, attach relevant images to complement your feedback.
  6.  Submit your feedback to be displayed on the user's profile.
  7.  At a later time, if desired, navigate back to your profile page.
  8.  Access the Feedback section.
  9.  Find the feedback/comment that you have provided to another user.
  10. Choose the option to delete your own feedback/comment.
- **Outcome:** Successfully contribute feedback to another user's profile and have the option to delete it later if needed, maintaining control over your contributions.
- **Screenshot:**
![UserReview](./docs/images/feedback-1.png)
  ![UserReview](./docs/images/feedback-2.png)
