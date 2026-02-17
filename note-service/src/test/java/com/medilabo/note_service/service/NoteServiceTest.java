package com.medilabo.note_service.service;

import com.medilabo.note_service.dto.NoteCreateDTO;
import com.medilabo.note_service.model.Note;
import com.medilabo.note_service.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note note1;
    private Note note2;

    @BeforeEach
    void setUp() {
        note1 = new Note(1, "First note");
        note1.setDate(LocalDateTime.now().minusDays(1));

        note2 = new Note(1, "Second note");
        note2.setDate(LocalDateTime.now());
    }

    @Test
    void getNotesByPatientId_shouldReturnSortedNotes() {
        // Arrange
        when(noteRepository.findNotesByPatientId(eq(1), any(Sort.class)))
                .thenReturn(List.of(note2, note1));

        // Act
        List<Note> result = noteService.getNotesByPatientId(1);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(note2, note1);

        // Vérifie que le tri DESC sur "date" est bien utilisé
        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(noteRepository).findNotesByPatientId(eq(1), sortCaptor.capture());

        Sort capturedSort = sortCaptor.getValue();
        assertThat(capturedSort.getOrderFor("date").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void getNotesByPatientId_shouldReturnEmptyList_whenNoNotes() {
        // Arrange
        when(noteRepository.findNotesByPatientId(eq(1), any(Sort.class)))
                .thenReturn(List.of());

        // Act
        List<Note> result = noteService.getNotesByPatientId(1);

        // Assert
        assertThat(result).isEmpty();
        verify(noteRepository).findNotesByPatientId(eq(1), any(Sort.class));
    }

    @Test
    void addNote_shouldCreateAndSaveNote() {
        // Arrange
        NoteCreateDTO dto = new NoteCreateDTO();
        dto.setPatientId(1);
        dto.setNoteText("New note");

        when(noteRepository.save(any(Note.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Note savedNote = noteService.addNote(dto);

        // Assert
        assertThat(savedNote.getPatientId()).isEqualTo(1);
        assertThat(savedNote.getNoteText()).isEqualTo("New note");
        assertThat(savedNote.getDate()).isNotNull();
        assertThat(savedNote.getDate()).isBeforeOrEqualTo(LocalDateTime.now());

        verify(noteRepository).save(any(Note.class));
    }
}
