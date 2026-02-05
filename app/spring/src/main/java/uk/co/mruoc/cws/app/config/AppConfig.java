package uk.co.mruoc.cws.app.config;

import java.util.Collection;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.task.ThreadPoolTaskExecutorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import uk.co.mruoc.cws.entity.WordsFactory;
import uk.co.mruoc.cws.image.DefaultImageDownloader;
import uk.co.mruoc.cws.usecase.AnswerDeleter;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.CandidateLoader;
import uk.co.mruoc.cws.usecase.CandidateRepository;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ClueRanker;
import uk.co.mruoc.cws.usecase.CompositeAnswerFinder;
import uk.co.mruoc.cws.usecase.CrosswordSolverFacade;
import uk.co.mruoc.cws.usecase.GridExtractor;
import uk.co.mruoc.cws.usecase.attempt.AsyncAttemptSolver;
import uk.co.mruoc.cws.usecase.attempt.AttemptCreator;
import uk.co.mruoc.cws.usecase.attempt.AttemptFinder;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;
import uk.co.mruoc.cws.usecase.attempt.AttemptService;
import uk.co.mruoc.cws.usecase.attempt.AttemptSolver;
import uk.co.mruoc.cws.usecase.attempt.AttemptSolverRunnableFactory;
import uk.co.mruoc.cws.usecase.attempt.AttemptUpdater;
import uk.co.mruoc.cws.usecase.attempt.BacktrackingAttemptSolver;
import uk.co.mruoc.cws.usecase.attempt.CompositeAttemptSolver;
import uk.co.mruoc.cws.usecase.attempt.GreedyAttemptSolver;
import uk.co.mruoc.cws.usecase.puzzle.ImageValidator;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleCreator;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleFinder;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleRepository;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleService;

@Configuration
@Slf4j
public class AppConfig {

  @Bean
  public CrosswordSolverFacade facade(
      PuzzleService puzzleService, AttemptService attemptService, AnswerDeleter answerDeleter) {
    return CrosswordSolverFacade.builder()
        .puzzleService(puzzleService)
        .attemptService(attemptService)
        .answerDeleter(answerDeleter)
        .build();
  }

  @Bean
  public PuzzleService puzzleService(PuzzleCreator creator, PuzzleFinder finder) {
    return PuzzleService.builder().creator(creator).finder(finder).build();
  }

  @Bean
  public PuzzleCreator puzzleCreator(
      ClueExtractor clueExtractor, GridExtractor gridExtractor, PuzzleRepository repository) {
    return PuzzleCreator.builder()
        .imageDownloader(new DefaultImageDownloader())
        .validator(new ImageValidator())
        .clueExtractor(clueExtractor)
        .gridExtractor(gridExtractor)
        .repository(repository)
        .wordsFactory(new WordsFactory())
        .build();
  }

  @Bean
  public PuzzleFinder puzzleFinder(PuzzleRepository repository) {
    return new PuzzleFinder(repository);
  }

  @Bean
  public AttemptService attemptService(
      AttemptCreator creator,
      AttemptFinder finder,
      AttemptUpdater updater,
      AsyncAttemptSolver solver) {
    return AttemptService.builder()
        .creator(creator)
        .finder(finder)
        .updater(updater)
        .asyncSolver(solver)
        .build();
  }

  @Bean
  public AttemptCreator attemptCreator(PuzzleFinder finder, AttemptRepository repository) {
    return AttemptCreator.builder().puzzleFinder(finder).repository(repository).build();
  }

  @Bean
  public AttemptUpdater attemptUpdater(AttemptFinder finder, AttemptRepository repository) {
    return AttemptUpdater.builder().finder(finder).repository(repository).build();
  }

  @Bean
  public AttemptFinder attemptFinder(AttemptRepository repository) {
    return new AttemptFinder(repository);
  }

  @Bean
  public CandidateLoader candidateLoader(
      CandidateRepository repository, AnswerFinder answerFinder, Executor executor) {
    return CandidateLoader.builder()
        .repository(repository)
        .answerFinder(answerFinder)
        .executor(executor)
        .build();
  }

  @Bean
  public GreedyAttemptSolver greedyAttemptSolver(AnswerFinder answerFinder, ClueRanker clueRanker) {
    return new GreedyAttemptSolver(answerFinder, clueRanker);
  }

  @Bean
  public BacktrackingAttemptSolver backtrackingAttemptSolver(CandidateLoader candidateLoader) {
    return new BacktrackingAttemptSolver(candidateLoader);
  }

  @Primary
  @Bean
  public CompositeAttemptSolver compositeAttemptSolver(
      BacktrackingAttemptSolver backtrackingSolver, GreedyAttemptSolver greedySolver) {
    int maxPasses = 5;
    // TODO configure max passes or store max passes and current passes against attempt
    return new CompositeAttemptSolver(backtrackingSolver, greedySolver, maxPasses);
  }

  @Bean
  public AttemptSolverRunnableFactory attemptSolverRunnableFactory(
      AttemptFinder finder, AttemptSolver solver, AttemptRepository repository) {
    return AttemptSolverRunnableFactory.builder()
        .finder(finder)
        .solver(solver)
        .repository(repository)
        .build();
  }

  @Bean
  public AsyncAttemptSolver asyncAttemptSolver(
      AttemptSolverRunnableFactory runnableFactory, Executor executor) {
    return AsyncAttemptSolver.builder().runnableFactory(runnableFactory).executor(executor).build();
  }

  @Bean
  public ThreadPoolTaskExecutor executorService(ThreadPoolTaskExecutorBuilder builder) {
    return builder.corePoolSize(20).maxPoolSize(20).queueCapacity(100).build();
  }

  @Bean
  public AnswerDeleter answerDeleter(AttemptFinder finder, AttemptRepository repository) {
    return AnswerDeleter.builder().finder(finder).repository(repository).build();
  }

  @Primary
  @Bean
  public AnswerFinder compositeAnswerFinder(Collection<AnswerFinder> finders) {
    log.info("creating composite answer finder with child finders {}", finders);
    return new CompositeAnswerFinder(finders);
  }
}
