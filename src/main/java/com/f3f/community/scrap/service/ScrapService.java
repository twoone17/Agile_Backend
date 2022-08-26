package com.f3f.community.scrap.service;

import com.f3f.community.exception.common.NotFoundByIdException;
import com.f3f.community.exception.postException.NotFoundPostByIdException;
import com.f3f.community.exception.scrapException.*;
import com.f3f.community.exception.userException.NotFoundUserException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.domain.ScrapPost;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.post.repository.ScrapPostRepository;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.f3f.community.common.constants.ResponseConstants.*;
import static com.f3f.community.scrap.dto.ScrapDto.*;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final ScrapPostRepository scrapPostRepository;

    // 스크랩 컬렉션 생성
    @Transactional
    public Long createScrap(SaveRequest saveRequest) throws Exception{

        Scrap newScrap = saveRequest.toEntity();
        User user = userRepository.findById(newScrap.getUser().getId()).get();
        List<Scrap> scraps = scrapRepository.findScrapsByUser(user);
        for (Scrap userScrap : scraps) {
            if (userScrap.getName().equals(newScrap.getName())) {
                throw new DuplicateScrapNameException();
            }
        }
        scrapRepository.save(newScrap);
        user.getScraps().add(newScrap);

        return newScrap.getId();
    }

    @Transactional
    public List<Post> getPosts(Long sid) {
        Scrap scrap = scrapRepository.findById(sid).orElseThrow(() -> new NotFoundByIdException("해당 아이디로 존재하는 스크랩을 찾을 수 없습니다."));
        List<Post> posts = new ArrayList<>();
        List<ScrapPost> scrapPostsByScrapId = scrapPostRepository.findScrapPostsByScrapId(sid);
        for (ScrapPost scrapPost : scrapPostsByScrapId) {
            posts.add(scrapPost.getPost());
        }
        return posts;

    }



    // 포스트에서 스크랩 저장 눌렀을때, 스크랩에 포스트
    @Transactional
    public Long saveCollection(Long scrapId, Long uid,Long postId) throws Exception {
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundByIdException::new);
        Post post = postRepository.findById(postId).orElseThrow(NotFoundByIdException::new);
        if (!scrap.getUser().getId().equals(uid)) {
            throw new NotFoundScrapByUserException();
        }
        if (!scrapPostRepository.existsByPostAndScrap(post, scrap)) {
            ScrapPost scrapPost = ScrapPost.builder().post(post).scrap(scrap).build();
            scrapPostRepository.save(scrapPost);
            scrap.getPostList().add(scrapPost);
            post.getScrapList().add(scrapPost);
            return scrapPost.getId();
        } else {
            throw new DuplicateScrapPostException();
        }


    }



    // 스크랩 컬렉션 이름 변경
    @Transactional
    public String updateCollectionName(Long scrapId, Long userId, String newName) throws Exception {
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundByIdException::new);
        User user = userRepository.findById(userId).orElseThrow(NotFoundByIdException::new);
        List<Scrap> scraps = scrapRepository.findScrapsByUser(user);
        for (Scrap userScrap : scraps) {
            if (userScrap.getName().equals(newName)) {
                throw new DuplicateScrapNameException();
            }
        }
        if (newName == null) {
            throw new NotFoundNewScrapNameException();
        }
        if (newName == "") {
            throw new NotFoundNewScrapNameException("스크랩 이름이 empty String입니다");
        }
        scrap.updateScrap(newName);

        return UPDATE;

    }


    // 스크랩 컬렉션 삭제
    @Transactional
    public String deleteCollection(Long scrapId, Long uid) throws Exception{
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundByIdException::new);
        if (scrap.getUser().getId().equals(uid)) {
            List<ScrapPost> remove = scrapPostRepository.findScrapPostsByScrap(scrap);
            scrapPostRepository.deleteAll(remove);
            scrapRepository.delete(scrap);
            return DELETE;
        } else {
            throw new NotFoundScrapByUserException();
        }

    }

    // 스크랩 컬렉션 아이템 삭제
    @Transactional
    public String deleteCollectionItem(Long scrapId, Long uid, Long postId) throws Exception{

        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundByIdException::new);
        if (scrap.getUser().getId().equals(uid)) {
            Post post = postRepository.findById(postId).orElseThrow(NotFoundByIdException::new);
            ScrapPost scrapPost = scrapPostRepository.findByScrapAndPost(scrap, post).orElseThrow(NotFoundScrapPostByScrapAndPostException::new);
            scrapPostRepository.delete(scrapPost);
            return DELETE;
        } else {
            throw new NotFoundScrapByUserException();
        }


    }



}
