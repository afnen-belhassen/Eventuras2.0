package main;

import entities.Categorie;
import entities.Event;
import entities.Post;
import entities.Comment;
import services.ServiceCategorie;
import services.ServiceEvent;
import services.ServicePost;
import services.ServiceComment;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;

public class Main {
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        ServiceCategorie sC = new ServiceCategorie();
        ServiceEvent sE = new ServiceEvent();
        ServicePost sP = new ServicePost();
        ServiceComment sCm = new ServiceComment();

        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Créez un event");
            System.out.println("2. Affichez tous les events et catégories");
            System.out.println("3. Modifier un event");
            System.out.println("4. Créez une catégorie");
            System.out.println("5. Supprimez un event");
            System.out.println("6. Supprimez une catégorie");
            System.out.println("7. Créez un post");
            System.out.println("8. Affichez tous les posts");
            System.out.println("9. Modifier un post");
            System.out.println("10. Supprimer un post");
            System.out.println("11. Ajouter un commentaire");
            System.out.println("12. Afficher tous les commentaires");
            System.out.println("13. Modifier un commentaire");
            System.out.println("14. Supprimer un commentaire");
            System.out.println("15. Quitter");

            System.out.print("Votre choix : ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1: // Ajouter un event
                        System.out.println("Entrez le titre de l'event :");
                        String title = scanner.nextLine();
                        System.out.println("Entrez la description :");
                        String description = scanner.nextLine();
                        System.out.println("Entrez la date (YYYY-MM-DD) :");
                        String dateStr = scanner.nextLine();
                        Date eventDate;
                        try {
                            eventDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                        } catch (ParseException e) {
                            System.out.println("Format de date invalide !");
                            break;
                        }
                        System.out.println("Entrez la localisation :");
                        String location = scanner.nextLine();
                        System.out.println("Entrez l'ID de la catégorie :");
                        int categoryId = scanner.nextInt();
                        scanner.nextLine();

                        Event event = new Event(0, title, description, eventDate, location, 1, categoryId);
                        sE.ajouter(event);
                        System.out.println("Event ajouté !");
                        break;

                    case 2: // Afficher tous les events et catégories
                        System.out.println("📅 Events : " + sE.afficherAll());
                        System.out.println("📂 Catégories : " + sC.afficherAll());
                        break;

                    case 3: // Modifier un event
                        System.out.print("ID de l'event à modifier : ");
                        int eventId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Nouveau titre : ");
                        String newTitle = scanner.nextLine();
                        Event updatedEvent = new Event(eventId, newTitle);
                        sE.update(updatedEvent);
                        System.out.println("Event mis à jour !");
                        break;

                    case 4: // Ajouter une catégorie
                        System.out.print("Nom de la catégorie : ");
                        String categoryName = scanner.nextLine();
                        Categorie categorie = new Categorie(categoryName);
                        sC.ajouter(categorie);
                        System.out.println("Catégorie ajoutée !");
                        break;

                    case 7: // Ajouter un post
                        System.out.print("Entrez l'ID de l'utilisateur : ");
                        int userId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Entrez le titre du post : ");
                        String postTitle = scanner.nextLine();
                        System.out.print("Entrez le contenu du post : ");
                        String postContent = scanner.nextLine();
                        Date createdAt = new Date();
                        Post post = new Post(0, postTitle, postContent, createdAt, userId);
                        sP.ajouter(post);
                        System.out.println("Post ajouté !");
                        break;

                    case 8: // Afficher les posts
                        System.out.println("📝 Posts : " + sP.afficherAll());
                        break;

                    case 9: // Modifier un post
                        System.out.print("ID du post à modifier : ");
                        int postId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Nouveau contenu : ");
                        String newContent = scanner.nextLine();
                        Post updatedPost = new Post(postId, newContent);
                        sP.update(updatedPost);
                        System.out.println("Post mis à jour !");
                        break;

                    case 11: // Ajouter un commentaire
                        System.out.print("ID du post : ");
                        int commentPostId = scanner.nextInt();
                        System.out.print("ID de l'utilisateur : ");
                        int commentUserId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Contenu du commentaire : ");
                        String commentContent = scanner.nextLine();
                        Comment comment = new Comment(0, commentPostId, commentUserId, commentContent, new Date());
                        sCm.ajouter(comment);
                        System.out.println("Commentaire ajouté !");
                        break;

                    case 12: // Afficher les commentaires
                        System.out.println("💬 Commentaires : " + sCm.afficherAll());
                        break;

                    case 13: // Modifier un commentaire
                        System.out.print("ID du commentaire à modifier : ");
                        int commentId = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Nouveau contenu : ");
                        String newCommentContent = scanner.nextLine();
                        Comment updatedComment = new Comment(commentId, newCommentContent);
                        sCm.update(updatedComment);
                        System.out.println("Commentaire mis à jour !");
                        break;

                    case 15: // Quitter
                        System.out.println("Programme terminé !");
                        scanner.close();
                        return;

                    default:
                        System.out.println("❌ Choix invalide !");
                }
            } catch (SQLException e) {
                System.out.println("🚨 Erreur SQL : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
