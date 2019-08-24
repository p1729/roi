package com.pankaj.roi.services;

import com.pankaj.roi.entities.Photo;
import com.pankaj.roi.entities.PhotoAlbum;
import com.pankaj.roi.entities.PhotoReaction;
import com.pankaj.roi.entities.User;
import com.pankaj.roi.enums.ReactionType;
import com.pankaj.roi.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class TransformationService {

    @Autowired
    private PhotoService photoService;

    public List<Pair<Photo, ReactionsData>> getPhotoReactionData(FBPhotos photos, User user) {
        return photos.getData().stream().map(photo ->
                Pair.of(getPhotoFrom(photo, user), Objects.isNull(photo.getReactions()) ? new ReactionsData() :
                        photo.getReactions()))
                .collect(Collectors.toList());
    }


    public Photo getPhotoFrom(FBPhotoData data, User user) {
        Photo photo = photoService.getPhotoByFbId(data.getId()).orElseGet(() -> Photo.builder()
                .fbId(data.getId())
                .fbLink(data.getLink())
                .imageLink(data.getPicture())
                .photoReactions(new HashSet<>())
                .users(new HashSet<>())
                .build());

        photo.addUser(user);
        if (Objects.nonNull(data.getAlbum()))
            photo.setAlbum(getPhotoAlbumFrom(data.getAlbum(), photo));
        if (Objects.nonNull(data.getReactions()))
            photo.addReactions(getPhotoReactionListFrom(data.getReactions()));
        return photo;
    }

    public PhotoAlbum getPhotoAlbumFrom(Album a, Photo photo) {
        PhotoAlbum album = photoService.getAlbumByFbId(a.getId()).orElseGet(() -> PhotoAlbum.builder()
                .fbId(a.getId())
                .photoList(new HashSet<>())
                .name(a.getName())
                .build());
        album.addPhoto(photo);
        return album;
    }

    public Set<PhotoReaction> getPhotoReactionListFrom(ReactionsData reactions) {
        if (Objects.isNull(reactions)) return Collections.emptySet();
        return reactions.getData().stream().map(this::getPhotoReactionFrom).collect(Collectors.toSet());
    }

    public PhotoReaction getPhotoReactionFrom(Reaction r) {
        return photoService.getReactionByFbId(r.getId()).orElseGet(() ->
                PhotoReaction.builder()
                        .fbId(r.getId())
                        .type(ReactionType.valueOf(r.getType().toUpperCase()))
                        .build());
    }

    public Set<PhotoAlbum> getSetOfAlbumFromPhotos(Set<Photo> photos) {
        return photos.stream().filter(Objects::nonNull)
                .map(Photo::getAlbum).filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


    public UserResponse getUserResponseFromUser(User user) {
        return UserResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .gender(user.getGender().name())
                .profilePicUrl(user.getProfilePicURL())
                .build();
    }
}