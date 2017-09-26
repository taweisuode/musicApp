package com.pengge.musicplayer.diyView;

/**
 * Created by pengge on 17/9/26.
 */
public class GridItem {
        private String image;
        private String title;
        private String playlist_id;
        private String listen_count;

        public GridItem() {
            super();
        }
        public String getThumb() {
            return image;
        }
        public void setThumb(String image) {
            this.image = image;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        public String getPlaylistId() {
            return this.playlist_id;
        }
        public void setPlaylistId(String playlist_id) {
            this.playlist_id = playlist_id;
        }
        public String getListenCount() {
            return this.listen_count;
        }
        public void setListenCount(String listen_count) {
            this.listen_count = listen_count;
        }
}
