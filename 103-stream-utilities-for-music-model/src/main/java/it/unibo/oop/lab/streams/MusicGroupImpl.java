package it.unibo.oop.lab.streams;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        final Stream<String> songNames = this.songs.stream().map(Song::getSongName);
        return songNames.sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        final Set<String> yearAlbums = new HashSet<>();
        for (final Map.Entry<String, Integer> entry : this.albums.entrySet()) {
            if (entry.getValue().equals(year)) {
                yearAlbums.add(entry.getKey());
            }
        }
        return yearAlbums.stream();
    }

    @Override
    public int countSongs(final String albumName) {
        int counter = 0;
        for (final Song s : this.songs) {
            if (s.getAlbumName().equals(Optional.of(albumName))) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public int countSongsInNoAlbum() {
        int counter = 0;
        for (final Song s : this.songs) {
            if (s.getAlbumName().isEmpty()) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        int counter = 0;
        double duration = 0;
        for (final Song s : this.songs) {
            if (s.getAlbumName().equals(Optional.of(albumName))) {
                duration = duration + s.getDuration();
                counter++;
            }
        }
        duration = duration / counter;
        return OptionalDouble.of(duration);
    }

    @Override
    public Optional<String> longestSong() {
        Song longestSong = new Song("", null, 0);
        for (final Song s : this.songs) {
            if (s.getDuration() > longestSong.getDuration()) {
                longestSong = s;
            }
        }
        return Optional.of(longestSong.getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        final Map<String, Double> albumDurations = new HashMap<>();
        for (final Map.Entry<String, Integer> entry : this.albums.entrySet()) {
            double totalDuration = 0;
            for (final Song s : this.songs) {
                if (s.getAlbumName().equals(Optional.of(entry.getKey()))) {
                    totalDuration = totalDuration + s.getDuration();
                }
            }
            albumDurations.put(entry.getKey(), totalDuration);
        }
        double maxDuration = 0;
        String longestAlbum = "";
        for (final Map.Entry<String, Double> entry : albumDurations.entrySet()) {
            if (entry.getValue() > maxDuration) {
                maxDuration = entry.getValue();
                longestAlbum = entry.getKey();
            }
        }
        return Optional.of(longestAlbum);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
