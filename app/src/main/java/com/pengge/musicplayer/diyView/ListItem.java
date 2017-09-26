package com.pengge.musicplayer.diyView;

/**
 * Created by pengge on 17/9/26.
 */
public class ListItem {
        private String title;
        private String playlist_id;
        private String song_id;
        private String author;
        private String album_id;
        private String album_title;
        private String all_artist_id;


        public ListItem() {
            super();
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public void setSongId(String song_id) {
            this.song_id = song_id;
        }
        public String getSongId() {
            return  this.song_id;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
        public String getAuthor() {
            return  this.author;
        }

        public void setAlbumId(String album_id) {
            this.album_id = album_id;
        }
        public String geAlbumId() {
            return  this.album_id;
        }

        public void setAlbumTitle(String album_title) {
            this.album_title = album_title;
        }
         public String getAlbumTitle() {
            return  this.album_title;
        }

        public void setAllArtistId(String all_artist_id) {
            this.all_artist_id = all_artist_id;
        }
        public String getAllArtistId() {
            return  this.all_artist_id;
        }
}
