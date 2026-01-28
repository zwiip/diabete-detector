package com.medilabo.note_service.config;

import com.medilabo.note_service.model.Note;
import com.medilabo.note_service.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initNotes(NoteRepository noteRepository) {
        return args -> {

            if (noteRepository.count() > 0) {
                noteRepository.deleteAll();
            }

            List<Note> notes = List.of(
                    new Note(1,
                            "Le patient déclare qu'il se sent très bien.\nPoids égal ou inférieur au poids recommandé.",
                            LocalDateTime.of(2025, 1, 1, 10, 0)
                    ),
                    new Note(2,
                            "Le patient déclare qu'il ressent beaucoup de stress au travail.\nIl se plaint également que son audition est anormale dernièrement.",
                            LocalDateTime.of(2025, 1, 2, 11, 30)
                    ),
                    new Note(2,
                            "Le patient déclare avoir fait une réaction aux médicaments au cours des 3 derniers mois.\nIl remarque également que son audition continue d'être anormale.",
                            LocalDateTime.of(2025, 2, 15, 9, 15)
                    ),
                    new Note(3,
                            "Le patient déclare qu'il fume depuis peu.",
                            LocalDateTime.of(2025, 3, 10, 14, 0)
                    ),
                    new Note(3,
                            "Le patient déclare qu'il est fumeur et qu'il a cessé de fumer l'année dernière.\nIl se plaint également de crises d’apnée respiratoire anormales.\nTests de laboratoire indiquant un taux de cholestérol LDL élevé.",
                            LocalDateTime.of(2025, 4, 5, 16, 45)
                    ),
                    new Note(4,
                            "Le patient déclare qu'il lui est devenu difficile de monter les escaliers.\nIl se plaint également d’être essoufflé.\nTests de laboratoire indiquant que les anticorps sont élevés.\nRéaction aux médicaments.",
                            LocalDateTime.of(2025, 5, 20, 10, 30)
                    ),
                    new Note(4,
                            "Le patient déclare qu'il a mal au dos lorsqu'il reste assis pendant longtemps.",
                            LocalDateTime.of(2025, 6, 18, 9, 0)
                    ),
                    new Note(4,
                            "Le patient déclare avoir commencé à fumer depuis peu.\nHémoglobine A1C supérieure au niveau recommandé.",
                            LocalDateTime.of(2025, 7, 22, 15, 20)
                    ),
                    new Note(4,
                            "Taille, Poids, Cholestérol, Vertige et Réaction",
                            LocalDateTime.of(2025, 8, 30, 8, 45)
                    )
            );

            noteRepository.saveAll(notes);
        };
    }
}
