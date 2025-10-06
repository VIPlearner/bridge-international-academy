package com.bridge.androidtechnicaltest.ui.common

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class BaseViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var testViewModel: TestBaseViewModel

    @Before
    fun setUp() {
        testViewModel = TestBaseViewModel()
    }

    @Test
    fun `showSuccessToast emits success toast event`() =
        runTest {
            // Given
            val message = "Operation successful"
            val expectedEvent = ToastEvent.Success(message)

            // When
            testViewModel.testShowSuccessToast(message)

            // Then
            val emittedEvent = testViewModel.toastEvents.first()
            assertEquals(expectedEvent, emittedEvent)
        }

    @Test
    fun `showErrorToast emits error toast event`() =
        runTest {
            // Given
            val message = "Something went wrong"
            val expectedEvent = ToastEvent.Error(message)

            // When
            testViewModel.testShowErrorToast(message)

            // Then
            val emittedEvent = testViewModel.toastEvents.first()
            assertEquals(expectedEvent, emittedEvent)
        }

    @Test
    fun `showInfoToast emits info toast event`() =
        runTest {
            // Given
            val message = "Information message"
            val expectedEvent = ToastEvent.Info(message)

            // When
            testViewModel.testShowInfoToast(message)

            // Then
            val emittedEvent = testViewModel.toastEvents.first()
            assertEquals(expectedEvent, emittedEvent)
        }

    @Test
    fun `showToast emits custom toast event`() =
        runTest {
            // Given
            val customEvent = ToastEvent.Success("Custom message")

            // When
            testViewModel.testShowToast(customEvent)

            // Then
            val emittedEvent = testViewModel.toastEvents.first()
            assertEquals(customEvent, emittedEvent)
        }

    @Test
    fun `multiple toast events are emitted in order`() =
        runTest {
            // Given
            val successMessage = "Success"
            val errorMessage = "Error"
            val infoMessage = "Info"

            val expectedEvents =
                listOf(
                    ToastEvent.Success(successMessage),
                    ToastEvent.Error(errorMessage),
                    ToastEvent.Info(infoMessage),
                )

            // When
            testViewModel.testShowSuccessToast(successMessage)
            testViewModel.testShowErrorToast(errorMessage)
            testViewModel.testShowInfoToast(infoMessage)

            // Then
            val emittedEvents = testViewModel.toastEvents.take(3).toList()
            assertEquals(expectedEvents, emittedEvents)
        }

    @Test
    fun `toast events are consumed only once`() =
        runTest {
            // Given
            val message = "Test message"

            // When
            testViewModel.testShowSuccessToast(message)

            // Collect the event once
            val firstEvent = testViewModel.toastEvents.first()

            // Try to collect again - this should not get the same event
            val collectJob =
                launch {
                    testViewModel.toastEvents.first()
                }

            // Give some time for collection attempt
            testDispatcher.scheduler.advanceTimeBy(100)

            // Then
            assertEquals(ToastEvent.Success(message), firstEvent)
            assertEquals(false, collectJob.isCompleted) // Should still be waiting for next event

            // Cleanup
            collectJob.cancel()
        }

    @Test
    fun `channel handles rapid successive toast events`() =
        runTest {
            // Given
            val numberOfEvents = 10
            val expectedEvents =
                (1..numberOfEvents).map {
                    ToastEvent.Info("Message $it")
                }

            // When
            expectedEvents.forEach { event ->
                testViewModel.testShowToast(event)
            }

            // Then
            val collectedEvents = testViewModel.toastEvents.take(numberOfEvents).toList()
            assertEquals(expectedEvents, collectedEvents)
        }

    @Test
    fun `empty message strings are handled correctly`() =
        runTest {
            // Given
            val emptyMessage = ""

            // When
            testViewModel.testShowSuccessToast(emptyMessage)
            testViewModel.testShowErrorToast(emptyMessage)
            testViewModel.testShowInfoToast(emptyMessage)

            // Then
            val events = testViewModel.toastEvents.take(3).toList()
            assertEquals(3, events.size)
            assertEquals(ToastEvent.Success(emptyMessage), events[0])
            assertEquals(ToastEvent.Error(emptyMessage), events[1])
            assertEquals(ToastEvent.Info(emptyMessage), events[2])
        }

    @Test
    fun `special characters in messages are preserved`() =
        runTest {
            // Given
            val specialMessage = "Test with émojis 🎉 and symbols @#$%"

            // When
            testViewModel.testShowSuccessToast(specialMessage)

            // Then
            val event = testViewModel.toastEvents.first()
            assertEquals(ToastEvent.Success(specialMessage), event)
        }

    // Test implementation of BaseViewModel to expose protected methods
    private class TestBaseViewModel : BaseViewModel() {
        fun testShowToast(event: ToastEvent) {
            showToast(event)
        }

        fun testShowSuccessToast(message: String) {
            showSuccessToast(message)
        }

        fun testShowErrorToast(message: String) {
            showErrorToast(message)
        }

        fun testShowInfoToast(message: String) {
            showInfoToast(message)
        }
    }
}
