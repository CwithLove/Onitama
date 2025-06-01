package Boundary;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ResourceLoader {
    
    /**
     * Charger une image depuis le jar
     * @param resourcePath Chemin de la ressource
     * @return BufferedImage
     * @throws IOException Si le fichier n'est pas trouvé ou si une erreur d'entrée/sortie se produit
     */
    public static BufferedImage loadImage(String resourcePath) throws IOException {
        // Traiter le chemin des ressources - Les ressources sont directement dans le jar, sans res/ prefix
        String jarResourcePath = resourcePath;
        if (jarResourcePath.startsWith("res/")) {
            jarResourcePath = jarResourcePath.substring(4); // Supprimer le prefix res/
        }
        
        // Ensurer que le chemin commence par /
        if (!jarResourcePath.startsWith("/")) {
            jarResourcePath = "/" + jarResourcePath;
        }
        
        // Premiere tentative de chargement depuis le jar
        InputStream inputStream = ResourceLoader.class.getResourceAsStream(jarResourcePath);
        
        if (inputStream != null) {
            try {
                return ImageIO.read(inputStream);
            } finally {
                inputStream.close();
            }
        }
        
        // dev mode
        try {
            return ImageIO.read(new java.io.File(resourcePath));
        } catch (IOException e) {
            throw new IOException("Cannot load reource. " + resourcePath, e);
        }
    }
    
    /**
     * Verifier si le fichier existe
     * @param resourcePath Path du fichier
     * @return true si le fichier existe, false sinon
     */
    public static boolean resourceExists(String resourcePath) {
        String jarResourcePath = resourcePath;
        if (jarResourcePath.startsWith("res/")) {
            jarResourcePath = jarResourcePath.substring(4);
        }
        
        if (!jarResourcePath.startsWith("/")) {
            jarResourcePath = "/" + jarResourcePath;
        }
        
        InputStream inputStream = ResourceLoader.class.getResourceAsStream(jarResourcePath);
        if (inputStream != null) {
            try {
                inputStream.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        
        // Verifier si le fichier existe dans le fichier systeme
        return new java.io.File(resourcePath).exists();
    }
} 
