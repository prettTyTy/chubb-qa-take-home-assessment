import { fireEvent, render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import { WizardStep1 } from '@/src/features/claims/components/wizard-step-1';

const nextStepMock = vi.hoisted(() => vi.fn());
const updateFormDataMock = vi.hoisted(() => vi.fn());

vi.mock('@/src/features/claims/stores/use-claim-wizard-store', () => ({
  useClaimWizardStore: () => ({
    formData: {},
    updateFormData: updateFormDataMock,
    nextStep: nextStepMock,
    fieldErrors: {},
  }),
}));

describe('WizardStep1', () => {
  it('allows the user to enter claim information', async () => {
    render(<WizardStep1 />);

    expect(
      screen.getByRole('heading', { name: 'Incident Details' })
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText(/when did the incident occur/i)
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText(/where did the incident occur/i)
    ).toBeInTheDocument();

    expect(
      screen.getByLabelText(/claim amount/i)
    ).toBeInTheDocument();

    expect(
      screen.getByRole('button', { name: 'Next' })
    ).toBeDisabled();
  });

  it('shows validation when the location is too short', async () => {
    const user = userEvent.setup();

    render(<WizardStep1 />);

    const locationInput = screen.getByLabelText(
      /where did the incident occur/i
    );

    await user.type(locationInput, 'ABC');
    await user.tab();

    expect(
      screen.getByText('Location must be at least 5 characters')
    ).toBeInTheDocument();
  });

  it('moves to the next step when the form is valid', async () => {
    const user = userEvent.setup();

    render(<WizardStep1 />);

    const dateInput = screen.getByLabelText(
      /when did the incident occur/i
    );

    const locationInput = screen.getByLabelText(
      /where did the incident occur/i
    );

    const amountInput = screen.getByLabelText(
      /claim amount/i
    );

    fireEvent.change(dateInput, {
      target: { value: '2026-09-18' },
    });

    await user.type(locationInput, '123 Main Street');
    await user.type(amountInput, '5000');

    const nextButton = screen.getByRole('button', {
      name: 'Next',
    });

    expect(nextButton).toBeEnabled();

    await user.click(nextButton);

    expect(updateFormDataMock).toHaveBeenCalled();
    expect(nextStepMock).toHaveBeenCalledTimes(1);
  });
});