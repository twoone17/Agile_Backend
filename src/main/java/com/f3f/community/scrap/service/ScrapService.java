package com.f3f.community.scrap.service;

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

import java.util.List;

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
//        User user = newScrap.getUser();
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





    // 포스트에서 스크랩 저장 눌렀을때, 스크랩에 포스트, 수정해야함
    @Transactional
    public Long saveCollection(Long scrapId, Long postId) throws Exception {
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundScrapByIdException::new);
        Post post = postRepository.findById(postId).orElseThrow(NotFoundPostByIdException::new);

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
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundScrapByIdException::new);
        User user = userRepository.findById(userId).orElseThrow(NotFoundUserException::new);
//        List<Scrap> scraps = user.getScraps();
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

        return "ok";

    }


    // 스크랩 컬렉션 삭제
    @Transactional
    public String deleteCollection(Long scrapId) throws Exception{
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundScrapByIdException::new);
        scrapRepository.delete(scrap);
        return "ok";
    }

    // 스크랩 컬렉션 아이템 삭제
    @Transactional
    public String deleteCollectionItem(Long scrapId, Long postId) throws Exception{

        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundScrapByIdException::new);
        Post post = postRepository.findById(postId).orElseThrow(NotFoundPostByIdException::new);
        ScrapPost scrapPost = scrapPostRepository.findByScrapAndPost(scrap, post).orElseThrow(NotFoundScrapPostByScrapAndPostException::new);
        scrap.getPostList().remove(scrapPost);
        post.getScrapList().remove(scrapPost);
        scrapPostRepository.delete(scrapPost);
        return "ok";

    }



}
