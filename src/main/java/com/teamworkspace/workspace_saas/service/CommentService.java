package com.teamworkspace.workspace_saas.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.teamworkspace.workspace_saas.dto.request.CommentRequest;
import com.teamworkspace.workspace_saas.dto.response.CommentResponse;
import com.teamworkspace.workspace_saas.entity.Comment;
import com.teamworkspace.workspace_saas.entity.Task;
import com.teamworkspace.workspace_saas.entity.User;
import com.teamworkspace.workspace_saas.exception.ResourceNotFoundException;
import com.teamworkspace.workspace_saas.repository.CommentRepository;
import com.teamworkspace.workspace_saas.repository.TaskRepository;
import com.teamworkspace.workspace_saas.repository.UserRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public CommentResponse createComment(CommentRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getAuthorId());
        User user = userOpt.orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<Task> taskOpt = taskRepository.findById(request.getTaskId());
        Task task = taskOpt.orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUser(user);
        comment.setTask(task);

        Comment savedComment = commentRepository.save(comment);

        return new CommentResponse(
            savedComment.getId(),
            savedComment.getContent(),
            savedComment.getCreatedAt(),
            savedComment.getTask().getId(),
            savedComment.getUser().getEmail()
        );

        
    }

    public List<CommentResponse> getAllComments() {
        List<Comment> comments = commentRepository.findAll();

        List<CommentResponse> responses = new ArrayList<>();

        for (Comment comment : comments) {
            CommentResponse response = new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getTask().getId(),
                comment.getUser().getEmail()
            );
            responses.add(response);
        }

        return responses;
    }

    public CommentResponse getCommentById(Long id) {

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        return new CommentResponse(
            comment.getId(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getTask().getId(),
            comment.getUser().getEmail()
        );
    }

    public CommentResponse updateComment(Long id, CommentRequest request) {

        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));


        comment.setContent(request.getContent());

        commentRepository.save(comment);

        return new CommentResponse(
            comment.getId(),
            comment.getContent(),
            comment.getCreatedAt(),
            comment.getTask().getId(),
            comment.getUser().getEmail()
        );
    }

    public String deleteComment(Long id) {
        
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        commentRepository.delete(comment);

        return "Comment deleted.";
    }

    
}
