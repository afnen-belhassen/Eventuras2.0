package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import entities.Comment;
import entities.Post;
import entities.user;
import javafx.stage.Stage;
import services.ServiceComment;
import services.ServicePost;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javafx.geometry.Pos;
import services.userService;

public class PostsController {
    @FXML private TextField TFTitle;
    @FXML private TextArea TFContent;
    @FXML private ImageView imageView;
    @FXML private VBox postsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button btnSearch;

    private final ServicePost servicePost = new ServicePost();
    private String imagePath = null;

    @FXML
    public void initialize() {
        filterComboBox.setValue("Tous");
        sortComboBox.setValue("Plus récents");
        afficherPosts();
        postsContainer.setAlignment(Pos.CENTER);
    }

    @FXML
    private void rechercher() {
        try {
            // Récupérer tous les posts depuis le service
            List<Post> allPosts = servicePost.afficherAll();
            String query = searchField.getText().trim().toLowerCase();
            String filter = filterComboBox.getValue();
            String sort = sortComboBox.getValue();

            // Filtrer selon le texte de recherche et le filtre choisi
            List<Post> filteredPosts = new java.util.ArrayList<>();
            for (Post post : allPosts) {
                boolean match = false;
                if (query.isEmpty()) {
                    match = true;
                } else {
                    switch (filter) {
                        case "Titre":
                            if (post.getTitle().toLowerCase().contains(query)) {
                                match = true;
                            }
                            break;
                        case "Contenu":
                            if (post.getContent().toLowerCase().contains(query)) {
                                match = true;
                            }
                            break;
                        case "Tous":
                        default:
                            if (post.getTitle().toLowerCase().contains(query) ||
                                    post.getContent().toLowerCase().contains(query)) {
                                match = true;
                            }
                            break;
                    }
                }
                if (match) {
                    filteredPosts.add(post);
                }
            }

            // Trier la liste selon l'option sélectionnée
            if (sort != null) {
                if (sort.equals("Plus récents")) {
                    filteredPosts.sort((p1, p2) -> p2.getCreated_at().compareTo(p1.getCreated_at()));
                } else if (sort.equals("Plus anciens")) {
                    filteredPosts.sort((p1, p2) -> p1.getCreated_at().compareTo(p2.getCreated_at()));
                }
            }

            // Mettre à jour l'affichage dans postsContainer
            postsContainer.getChildren().clear();
            for (Post post : filteredPosts) {
                VBox postBox = createPostBox(post);
                postsContainer.getChildren().add(postBox);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Affiche tous les posts
    private void afficherPosts() {
        postsContainer.getChildren().clear();
        try {
            List<Post> posts = servicePost.afficherAll();
            System.out.println(posts);
            for (Post post : posts) {
                VBox postBox = createPostBox(post);
                postsContainer.getChildren().add(postBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createPostBox(Post post) throws SQLException {
        VBox postBox = new VBox();
        postBox.setSpacing(10);
        postBox.setAlignment(Pos.CENTER_LEFT);
        postBox.getStyleClass().add("card");

        // 1. Récupération des informations de l'auteur du post
        userService serviceUser = new userService();
        user author = serviceUser.getUserById(post.getUser_id());

        // Zone d'info utilisateur (photo et username)
        HBox userInfoBox = new HBox();
        userInfoBox.setSpacing(10);
        userInfoBox.setAlignment(Pos.CENTER_LEFT);
        ImageView userImageView = new ImageView();
        userImageView.setFitWidth(40);
        userImageView.setFitHeight(40);
        if (author != null && author.getPicture() != null && !author.getPicture().isEmpty()) {
            Image userImage = new Image(getClass().getResourceAsStream("/Images/" + author.getPicture()));
            if (!userImage.isError()) {
                userImageView.setImage(userImage);
                // Arrondir la photo avec un clip circulaire
                Circle clip = new Circle(20, 20, 20);
                userImageView.setClip(clip);
            }
        }
        Label usernameLabel = new Label(author != null ? author.getUsername() : "Inconnu");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        userInfoBox.getChildren().addAll(userImageView, usernameLabel);

        // 2. Titre du post
        Label titleLabel = new Label(post.getTitle());
        titleLabel.getStyleClass().add("card-title");

        // 3. Informations supplémentaires : temps relatif depuis la création
        String relativeTime = getRelativeTime(post.getCreated_at());
        Label postInfoLabel = new Label(relativeTime);
        postInfoLabel.getStyleClass().add("card-info");

        // 4. Contenu du post
        Label contentLabel = new Label(post.getContent());
        contentLabel.getStyleClass().add("card-content");
        contentLabel.setWrapText(true);

        // 5. Nombre de commentaires
        int commentCount = servicePost.getCommentCount(post.getId());
        Label commentCountLabel = new Label("Commentaires: " + commentCount);

        // 6. Image du post (ajoutée uniquement si présente)
        ImageView postImageView = null;
        if (post.getImage_path() != null && !post.getImage_path().isEmpty()) {
            postImageView = new ImageView();
            postImageView.setPreserveRatio(true);
            postImageView.setFitWidth(250);
            Image img = new Image("file:" + post.getImage_path());
            postImageView.setImage(img);
        }

        // 7. Système de like (existant)
        services.ServiceLike serviceLike = new services.ServiceLike();
        user currentUser = utils.Session.getInstance().getCurrentUser();
        int currentUserId = (currentUser != null) ? currentUser.getId() : -1;
        int likeCount = serviceLike.getNombreLikes(post.getId());
        boolean alreadyLiked = false;
        if (currentUserId != -1) {
            alreadyLiked = serviceLike.aDejaLike(currentUserId, post.getId());
        }
        Label likeCountLabel = new Label("Likes: " + likeCount);
        Button likeButton = new Button(alreadyLiked ? "Unlike" : "Like");
        likeButton.setOnAction(e -> {
            try {
                if (serviceLike.aDejaLike(currentUserId, post.getId())) {
                    serviceLike.supprimerLike(currentUserId, post.getId());
                    likeButton.setText("Like");
                } else {
                    serviceLike.ajouterLike(currentUserId, post.getId());
                    likeButton.setText("Unlike");
                }
                int newLikeCount = serviceLike.getNombreLikes(post.getId());
                likeCountLabel.setText("Likes: " + newLikeCount);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // 8. Bouton pour afficher/masquer la zone de commentaires
        Button toggleCommentsBtn = new Button("Afficher commentaires");
        toggleCommentsBtn.setOnAction(e -> {
            VBox commentsContainer = null;
            for (javafx.scene.Node node : postBox.getChildren()) {
                if (node instanceof VBox && "commentsContainer".equals(node.getId())) {
                    commentsContainer = (VBox) node;
                    break;
                }
            }
            if (commentsContainer != null) {
                postBox.getChildren().remove(commentsContainer);
                toggleCommentsBtn.setText("Afficher commentaires");
                // Mettre à jour le nombre de commentaires après réduction
                try {
                    int newCount = servicePost.getCommentCount(post.getId());
                    commentCountLabel.setText("Commentaires: " + newCount);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            } else {
                VBox newCommentsContainer = createCommentsContainer(post, postBox, toggleCommentsBtn, commentCountLabel);
                postBox.getChildren().add(newCommentsContainer);
                toggleCommentsBtn.setText("Réduire");
            }
        });

        // Assemblage final de la "card"
        postBox.getChildren().addAll(userInfoBox, titleLabel, postInfoLabel, contentLabel);
        if (postImageView != null) {
            HBox imageContainer = new HBox();
            imageContainer.setAlignment(Pos.CENTER);
            imageContainer.getChildren().add(postImageView);
            postBox.getChildren().add(imageContainer);
        }

        HBox likeCommentBox = new HBox();
        likeCommentBox.setSpacing(10);
        likeCommentBox.setAlignment(Pos.CENTER_LEFT);
        likeCommentBox.getChildren().addAll(likeCountLabel, likeButton, commentCountLabel, toggleCommentsBtn);
        postBox.getChildren().add(likeCommentBox);

        return postBox;
    }


    // Dans votre PostsController.java

    private HBox createCommentBox(Comment c) throws SQLException {
        HBox commentBox = new HBox();
        commentBox.setSpacing(10);
        commentBox.setAlignment(Pos.TOP_LEFT);
        commentBox.getStyleClass().add("comment-box");

        userService serviceUser = new userService();
        user commenter = serviceUser.getUserById(c.getUser_id());

        ImageView commenterImageView = new ImageView();
        commenterImageView.setFitWidth(30);
        commenterImageView.setFitHeight(30);
        if (commenter != null && commenter.getPicture() != null && !commenter.getPicture().isEmpty()) {
            Image commenterImage = new Image(getClass().getResourceAsStream("/Images/" + commenter.getPicture()));
            if (!commenterImage.isError()) {
                commenterImageView.setImage(commenterImage);
                javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(15, 15, 15);
                commenterImageView.setClip(clip);
            }
        }

        VBox commentContentBox = new VBox();
        commentContentBox.setSpacing(3);
        commentContentBox.setAlignment(Pos.TOP_LEFT);

        Label usernameLabel = new Label(commenter != null ? commenter.getUsername() : "Inconnu");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        Label commentTextLabel = new Label(c.getContent());
        commentTextLabel.setWrapText(true);
        commentTextLabel.setStyle("-fx-font-size: 12px;");

        String commentRelativeTime = getRelativeTime(c.getCreated_at());
        System.out.println("comment : datetime "+c.getCreated_at());
        Label timeLabel = new Label("(" + commentRelativeTime + ")");
        timeLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        commentContentBox.getChildren().addAll(usernameLabel, commentTextLabel, timeLabel);
        commentBox.getChildren().addAll(commenterImageView, commentContentBox);

        return commentBox;
    }

    // Modification de la méthode createCommentsContainer pour utiliser createCommentBox
    private VBox createCommentsContainer(Post post, VBox postBox, Button toggleCommentsBtn, Label commentCountLabel) {
        VBox commentsContainer = new VBox();
        commentsContainer.setSpacing(5);
        commentsContainer.setStyle("-fx-padding: 5; -fx-background-color: #e9e9e9; -fx-border-color: #ccc;");
        commentsContainer.setId("commentsContainer");

        ServiceComment serviceComment = new ServiceComment();
        try {
            List<Comment> comments = serviceComment.getCommentsByPostId(post.getId());
            for (Comment c : comments) {
                HBox commentBox = createCommentBox(c);
                commentsContainer.getChildren().add(commentBox);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Zone pour écrire un nouveau commentaire et bouton pour l'ajouter
        HBox addCommentBox = new HBox();
        addCommentBox.setSpacing(5);
        TextField TFNewComment = new TextField();
        TFNewComment.setPromptText("Écrire un commentaire...");
        Button btnAjouterComment = new Button("Ajouter");
        addCommentBox.getChildren().addAll(TFNewComment, btnAjouterComment);

        btnAjouterComment.setOnAction(e -> {
            String newCommentText = TFNewComment.getText().trim();
            if (!newCommentText.isEmpty()) {
                try {
                    user currentUser = utils.Session.getInstance().getCurrentUser();
                    if (currentUser == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Aucun utilisateur connecté !");
                        alert.showAndWait();
                        return;
                    }
                    int currentUserId = currentUser.getId();
                    Comment newComment = new Comment(0, post.getId(), currentUserId, newCommentText, new Date());
                    ServiceComment serviceComment2 = new ServiceComment();
                    serviceComment2.ajouter(newComment);
                    // Rafraîchir le conteneur des commentaires
                    VBox refreshedContainer = createCommentsContainer(post, postBox, toggleCommentsBtn, commentCountLabel);
                    int index = postBox.getChildren().indexOf(commentsContainer);
                    postBox.getChildren().set(index, refreshedContainer);
                    // Mettre à jour le nombre de commentaires dans la carte
                    int newCount = servicePost.getCommentCount(post.getId());
                    commentCountLabel.setText("Commentaires: " + newCount);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        commentsContainer.getChildren().add(addCommentBox);
        return commentsContainer;
    }

    // Ajout d'un nouveau post
    @FXML
    private void ajouter() {
        String title = TFTitle.getText();
        String content = TFContent.getText();

        if (title.isEmpty() || content.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            alert.show();
            return;
        }

        // Récupérer l'utilisateur courant depuis la session
        user currentUser = utils.Session.getInstance().getCurrentUser();
        int currentUserId = 25;
        if (currentUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Aucun utilisateur connecté !");
            alert.show();
            //return;
        }
        else{
        currentUserId = currentUser.getId();
        }
        try {
            Timestamp createdAt = new Timestamp(System.currentTimeMillis());
            // Utiliser l'ID de l'utilisateur courant au lieu de la valeur fixe "1"
            Post newPost = new Post(0, title, content, createdAt, currentUserId, imagePath);
            servicePost.ajouter(newPost);
            afficherPosts();
            TFTitle.clear();
            TFContent.clear();
            imageView.setImage(null);
            imagePath = null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Upload d'une image pour le post
    @FXML
    private void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            Image img = new Image("file:" + imagePath);
            imageView.setImage(img);
        }
    }

    // Méthode utilitaire pour convertir une date en temps relatif (ex: "Il y a 2 h", "Il y a 5 m")
    private String getRelativeTime(Date date) {
        long diffMillis = System.currentTimeMillis() - date.getTime();
        System.out.println("date "+date);


        // Si la date est dans le futur
        if(diffMillis < 0) {
            return "À l'avenir";
        }

        long diffSeconds = diffMillis / 1000;
        if(diffSeconds < 60) {
            return "Il y a " + diffSeconds + " s";
        }

        long diffMinutes = diffSeconds / 60;
        System.out.println("System.currentTimeMillis() "+System.currentTimeMillis());
        System.out.println("date.getTime() "+date.getTime());

        if(diffMinutes < 60) {
            return "Il y a " + diffMinutes + " m";
        }

        long diffHours = diffMinutes / 60;
        if(diffHours < 24) {
            return "Il y a " + diffHours + " h";
        }

        long diffDays = diffHours / 24;
        return "Il y a " + diffDays + " j";
    }

}
