package com.f3f.community.scrap.service;

import com.f3f.community.exception.postException.NotFoundPostByIdException;
import com.f3f.community.exception.scrapException.*;
import com.f3f.community.exception.userException.NotFoundUserByIdException;
import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.repository.ScrapRepository;
import com.f3f.community.user.domain.User;
import com.f3f.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.f3f.community.scrap.dto.ScrapDto.*;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    // 스크랩 컬렉션 생성
    @Transactional
    public Long createScrap(SaveRequest saveRequest) throws Exception{
        if (saveRequest.getName() == null) {
            throw new NotFoundScrapNameException();
        }
        if (saveRequest.getUser() == null) {
            throw new NotFoundScrapUserException();
        }
        if (saveRequest.getPostList() == null) {
            throw new NotFoundScrapPostListException();
        }
        Scrap newScrap = saveRequest.toEntity();
        User user = newScrap.getUser();
        List<Scrap> scraps = scrapRepository.findScrapsByUser(newScrap.getUser());
        for (Scrap userScrap : scraps) {
            if (userScrap.getName().equals(newScrap.getName())) {
                throw new DuplicateScrapNameException();
            }
        }
        scrapRepository.save(newScrap);
        user.getScraps().add(newScrap);
        userRepository.save(user);

        return newScrap.getId();
    }


    // 스크랩 컬렉션에 해당 게시글 리스트 가져오기
    @Transactional(readOnly = true)
    public List<Post> findAllByCollection(Long scrapId) throws Exception {

        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundScrapByIdException::new);
        return scrap.getPostList();

    }

    // 포스트에서 스크랩 저장 눌렀을때, 스크랩에 포스트, 수정해야함
    @Transactional
    public void saveCollection(Long scrapId, Long postId) throws Exception{
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundScrapByIdException::new);
        Post post = postRepository.findById(postId).orElseThrow(NotFoundPostByIdException::new);
        if (!scrap.getPostList().contains(post)) {
            scrap.getPostList().add(post);
            scrapRepository.save(scrap);
        }
    }



    // 스크랩 컬렉션 이름 변경
    @Transactional
    public String updateCollectionName(Long scrapId, Long userId, String newName) throws Exception {
        Scrap scrap = scrapRepository.findById(scrapId).orElseThrow(NotFoundScrapByIdException::new);
        User user = userRepository.findById(userId).orElseThrow(NotFoundUserByIdException::new);
        List<Scrap> scraps = user.getScraps();
        for (Scrap userScrap : scraps) {
            if (userScrap.getName().equals(newName)) {
                throw new DuplicateScrapNameException();
            }
        }
        scrap.updateScrap(newName);
        scrapRepository.save(scrap);

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
        scrap.getPostList().remove(post);
        scrapRepository.save(scrap);
        return "ok";
    }



}
