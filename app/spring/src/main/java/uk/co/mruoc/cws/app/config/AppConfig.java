package uk.co.mruoc.cws.app.config;

import java.time.Clock;
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
import uk.co.mruoc.cws.solver.tesseract.OpenCvGridImageFactory;
import uk.co.mruoc.cws.usecase.AnswerDeleter;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.CandidateLoader;
import uk.co.mruoc.cws.usecase.CandidateRepository;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ClueRanker;
import uk.co.mruoc.cws.usecase.ClueTypePolicy;
import uk.co.mruoc.cws.usecase.CluesFactory;
import uk.co.mruoc.cws.usecase.CompositeAnswerFinder;
import uk.co.mruoc.cws.usecase.CrosswordSolverFacade;
import uk.co.mruoc.cws.usecase.GridExtractor;
import uk.co.mruoc.cws.usecase.UUIDSupplier;
import uk.co.mruoc.cws.usecase.attempt.AsyncAttemptSolver;
import uk.co.mruoc.cws.usecase.attempt.AttemptCreator;
import uk.co.mruoc.cws.usecase.attempt.AttemptDeleter;
import uk.co.mruoc.cws.usecase.attempt.AttemptFinder;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;
import uk.co.mruoc.cws.usecase.attempt.AttemptService;
import uk.co.mruoc.cws.usecase.attempt.AttemptSolver;
import uk.co.mruoc.cws.usecase.attempt.AttemptSolverRunnableFactory;
import uk.co.mruoc.cws.usecase.attempt.AttemptSummaryRepository;
import uk.co.mruoc.cws.usecase.attempt.AttemptUpdater;
import uk.co.mruoc.cws.usecase.attempt.BacktrackingAttemptSolver;
import uk.co.mruoc.cws.usecase.attempt.CompositeAttemptSolver;
import uk.co.mruoc.cws.usecase.attempt.GreedyAttemptSolver;
import uk.co.mruoc.cws.usecase.puzzle.ImageValidator;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleCreator;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleFinder;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleRepository;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleService;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleSummaryRepository;

@Configuration
@Slf4j
public class AppConfig {

  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }

  @Bean
  public CrosswordSolverFacade facade(
      PuzzleService puzzleService, AttemptService attemptService, AnswerDeleter answerDeleter) {
    return CrosswordSolverFacade.builder()
        .puzzleService(puzzleService)
        .attemptService(attemptService)
        .answerDeleter(answerDeleter)
        .gridImageFactory(new OpenCvGridImageFactory())
        .build();
  }

  @Bean
  public PuzzleService puzzleService(PuzzleCreator creator, PuzzleFinder finder) {
    return PuzzleService.builder().creator(creator).finder(finder).build();
  }

  @Bean
  public PuzzleCreator puzzleCreator(
      CluesFactory cluesFactory,
      GridExtractor gridExtractor,
      PuzzleRepository repository,
      Clock clock) {
    return PuzzleCreator.builder()
        .imageDownloader(new DefaultImageDownloader())
        .validator(new ImageValidator())
        .idSupplier(new UUIDSupplier())
        .cluesFactory(cluesFactory)
        .gridExtractor(gridExtractor)
        .repository(repository)
        .wordsFactory(new WordsFactory())
        .clock(clock)
        .build();
  }

  @Bean
  public PuzzleFinder puzzleFinder(
      PuzzleRepository puzzleRepository, PuzzleSummaryRepository summaryRepository) {
    return PuzzleFinder.builder()
        .puzzleRepository(puzzleRepository)
        .summaryRepository(summaryRepository)
        .build();
  }

  @Bean
  public AttemptService attemptService(
      AttemptCreator creator,
      AttemptFinder finder,
      AttemptUpdater updater,
      AttemptDeleter deleter,
      AsyncAttemptSolver solver) {
    return AttemptService.builder()
        .creator(creator)
        .finder(finder)
        .updater(updater)
        .deleter(deleter)
        .asyncSolver(solver)
        .build();
  }

  @Bean
  public AttemptCreator attemptCreator(
      PuzzleFinder finder, AttemptRepository repository, Clock clock) {
    return AttemptCreator.builder()
        .puzzleFinder(finder)
        .repository(repository)
        .idSupplier(new UUIDSupplier())
        .clock(clock)
        .build();
  }

  @Bean
  public AttemptUpdater attemptUpdater(AttemptFinder finder, AttemptRepository repository) {
    return AttemptUpdater.builder().finder(finder).repository(repository).build();
  }

  @Bean
  public AttemptDeleter attemptDeleter(AttemptRepository repository) {
    return new AttemptDeleter(repository);
  }

  @Bean
  public AttemptFinder attemptFinder(
      AttemptRepository attemptRepository, AttemptSummaryRepository summaryRepository) {
    return AttemptFinder.builder()
        .attemptRepository(attemptRepository)
        .summaryRepository(summaryRepository)
        .build();
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
  public GreedyAttemptSolver greedyAttemptSolver(
      AnswerFinder answerFinder, ClueRanker clueRanker, AttemptRepository attemptRepository) {
    return new GreedyAttemptSolver(answerFinder, clueRanker, attemptRepository);
  }

  @Bean
  public BacktrackingAttemptSolver backtrackingAttemptSolver(
      CandidateLoader candidateLoader, AttemptRepository attemptRepository) {
    return new BacktrackingAttemptSolver(candidateLoader, attemptRepository);
  }

  @Primary
  @Bean
  public CompositeAttemptSolver compositeAttemptSolver(
      BacktrackingAttemptSolver backtrackingSolver,
      GreedyAttemptSolver greedySolver,
      AttemptRepository repository) {
    return CompositeAttemptSolver.builder()
        .backtrackingSolver(backtrackingSolver)
        .greedySolver(greedySolver)
        .repository(repository)
        // TODO configure max passes or store max passes and current passes against attempt
        .maxPasses(5)
        .build();
  }

  @Bean
  public AttemptSolverRunnableFactory attemptSolverRunnableFactory(
      AttemptFinder finder, AttemptSolver solver, AttemptUpdater updater) {
    return AttemptSolverRunnableFactory.builder()
        .finder(finder)
        .solver(solver)
        .updater(updater)
        .build();
  }

  @Bean
  public AsyncAttemptSolver asyncAttemptSolver(
      AttemptUpdater updater, AttemptSolverRunnableFactory runnableFactory, Executor executor) {
    return AsyncAttemptSolver.builder()
        .updater(updater)
        .runnableFactory(runnableFactory)
        .executor(executor)
        .build();
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

  @Bean
  public CluesFactory cluesFactory(ClueExtractor clueExtractor, ClueTypePolicy clueTypePolicy) {
    return CluesFactory.builder()
        .clueExtractor(clueExtractor)
        .clueTypePolicy(clueTypePolicy)
        .build();
  }
}
