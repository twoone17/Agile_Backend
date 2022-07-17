package com.f3f.community.scrap.service;

import com.f3f.community.post.domain.Post;
import com.f3f.community.post.repository.PostRepository;
import com.f3f.community.scrap.domain.Scrap;
import com.f3f.community.scrap.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScrapService {
    /*
    게시글 조회,
    스크랩 컬렉션 이름 변경
     */
    private final ScrapRepository scrapRepository;
    private final PostRepository postRepository;

    // 전체 스크랩 컬렉션 가져오기
    public List<Scrap> findAll() {
        return scrapRepository.findAll();
    }

    // 스크랩 컬렉션에 해당 게시글 리스트 가져오기
    public List<Post> findAllByCollection(Scrap collection) {
        return collection.getPostList();
    }

    // 포스트에서 스크랩 저장 눌렀을때, 스크랩 저장
    public void saveCollection(Long scrapId, Long postId) {
        Scrap scrap = scrapRepository.findByIdScrap(scrapId);
        Post post = postRepository.findByIdPost(postId);

        scrap.getPostList().add(post);
        scrapRepository.save(scrap);

    }


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
    // 스크랩 컬렉션 이름 변경


// 스크랩 컬렉션 삭제

}
