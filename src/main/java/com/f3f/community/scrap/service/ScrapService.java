package com.f3f.community.scrap.service;

import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;

    // 전체 스크랩 컬렉션 가져오기
    public List<Scrap> findAll() {
        return scrapRepository.findAll();
    }

    // 스크랩 컬렉션에 해당 게시글 리스트 가져오기
    public List<Post> findAllByCollection(Long scrapId) {
        Scrap scrap = scrapRepository.findByIdScrap(scrapId);
        return scrap.getPostList();
    }

    // 포스트에서 스크랩 저장 눌렀을때, 스크랩 저장
    public void saveCollection(Long scrapId, Long postId) {
        Scrap scrap = scrapRepository.findByIdScrap(scrapId);
        Post post = postRepository.findByIdPost(postId);

        scrap.getPostList().add(post);
        scrapRepository.save(scrap);

    }


    // 스크랩 컬렉션 이름 변경
    public void updateCollectionName(Long scrapId, String newName) throws Exception {
        Scrap scrap = scrapRepository.findByIdScrap(scrapId);
        boolean existsById = scrapRepository.existsById(scrapId);
        if (!existsById) {
            if (scrap.getName().equals(newName)) {
                throw new IllegalStateException();
            } else {
                scrap.updateScrap(newName);
            }
        }else {
            throw new IllegalArgumentException();
        }
        scrapRepository.save(scrap);
    }


    // 스크랩 컬렉션 삭제
    public void deleteCollection(Long scrapId) {
        Scrap scrap = scrapRepository.findByIdScrap(scrapId);
        scrapRepository.delete(scrap);
    }

    // 스크랩 컬렉션 아이템 삭제
    public void deleteCollectionItem(Long scrapId, Long postId) throws Exception{


        if (postRepository.existsById(postId)) { // 게시글이 존재하는지 확인
            if (scrapRepository.existsById(scrapId)) { // 해당 스크랩 컬렉션이 존재하는지 확인
                Scrap scrap = scrapRepository.findByIdScrap(scrapId);
                Post post = postRepository.findByIdPost(postId);
                scrap.getPostList().remove(post); // remove 메소드로 제거하였는데, 성능 문제는 없는지
                scrapRepository.save(scrap);
            } else { // 스크랩 컬렉션 없으면 터지는 예외
                throw new IllegalArgumentException();
            }
        } else { // 게시글 없으면 터지는 예외
            throw new IllegalArgumentException();
        }


    }





}
