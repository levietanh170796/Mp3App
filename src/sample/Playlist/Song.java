package sample.Playlist;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by NgThach96 on 5/13/2017.
 */
public class Song {

    private File mFile;
    private String mTitle;
    private String mArtis;
    private String mAlbum;
    private String mGenere;
    private String mDuration;
    private Image mImage = null;
    //private int mLenghtInSecond;

    public long getmDuration() {
        return (long) Double.parseDouble(mDuration)/1000;
    }

    public Song(File file) {
        mFile = file;
        try {
            InputStream input = new FileInputStream(mFile);
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(input, handler, metadata, parseCtx);
            input.close();
            mImage = getImageFromMp3(file.getAbsolutePath());
            mTitle = metadata.get("title");
            if(mTitle == null) mTitle = "Unknown";
            mArtis = metadata.get("xmpDM:artist");
            mDuration = metadata.get("xmpDM:duration");
            if (mArtis == null) mArtis = "Unknown";
            mGenere = metadata.get("xmpDM:genre");
            if (mGenere == null) mGenere = "Unknown";
            mAlbum = metadata.get("xmpDM:album");
            if (mAlbum == null) mAlbum = "Unknown";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return mFile + "\n" + mTitle + "\n" + mArtis + "\n" + mAlbum + "\n" + mGenere + "\n";
    }

    public File getmFile() {
        return mFile;
    }

    public void setmFile(File mFile) {
        this.mFile = mFile;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmArtis() {
        return mArtis;
    }

    public void setmArtis(String mArtis) {
        this.mArtis = mArtis;
    }

    public String getmAlbum() {
        return mAlbum;
    }

    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Song) {
            if (((Song) obj).getmTitle() != null && this.mTitle != null) {

                if (((Song) obj).getmTitle().equalsIgnoreCase(this.mTitle)) return true;
            }
        }
        return false;
    }

    private Image getImageFromMp3(String filePath) {

        Image card = null;
        Mp3File song = null;
        try {
            song = new Mp3File(filePath);
            if (song.hasId3v2Tag()) {
                ID3v2 id3v2tag = song.getId3v2Tag();
                byte[] imageData = id3v2tag.getAlbumImage();
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
                card = SwingFXUtils.toFXImage(img, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            card = null;
        }
        return card;
    }

    public Image getmImage() {
        return mImage;
    }
}
